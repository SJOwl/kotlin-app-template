package trading.statisics

import org.nield.kotlinstatistics.descriptiveStatistics
import ru.tinkoff.invest.openapi.models.market.Candle
import trading.infrastructure.firstAfter
import trading.infrastructure.logger
import java.lang.Integer.max
import java.math.BigDecimal
import java.time.Duration

data class InvestStats(
    val periodDays: Int,
    val profit: BigDecimal
)

data class Stat(
    val mean: Double,
    val standardDeviation: Double
)

class DailyInvestmentOutWithStats(
    private val investmentPeriodDays: Long,
    private val maxDaysAfter: Long,
    private val deviationsBandwidth: Double
) {

    fun values(
        history: List<Candle>,
        start: Int = 0,
        end: Int = history.size - 1
    ): List<InvestStats> {

        val processor = DailyInvestmentsProfitProcessor(investmentPeriodDays)

        val values = mutableListOf<InvestStats>()

        val latestTime = history[end].time.minusDays(investmentPeriodDays + maxDaysAfter + 1)
        val lastIndex = history.firstAfter(start, end) { !it.time.isBefore(latestTime) }

        val t = System.currentTimeMillis()
        val stats = (0..end).map {
            processor.values(history, 0, it).descriptiveStatistics
        }.map {
            Stat(it.mean, it.standardDeviation)
        }

        logger.info("got all stats for ${System.currentTimeMillis() - t} ms")

        for (i in max((investmentPeriodDays + maxDaysAfter).toInt(), start) until lastIndex) {
            val buyPrice = history[i].highestPrice

            var j = i + 1
            while (j < end - 1 && !conditions(history, i, j, stats[j])) {
                j++
            }
            values.add(
                InvestStats(
                    Duration.between(history[i].time, history[j].time).toDays().toInt(),
                    history[j].highestPrice / buyPrice
                )
            )
        }

        return values
    }

    private fun conditions(
        history: List<Candle>,
        buyIndex: Int,
        currentIndex: Int,
        stat: Stat
    ): Boolean {
        val duration = Duration.between(history[buyIndex].time, history[currentIndex].time).toDays()
        val profit = history[currentIndex].highestPrice.toDouble() / history[buyIndex].highestPrice.toDouble()

        if (duration < investmentPeriodDays && profit > stat.mean + stat.standardDeviation * deviationsBandwidth) return true
        if (duration > investmentPeriodDays && duration < investmentPeriodDays + maxDaysAfter && profit < stat.mean - stat.standardDeviation * deviationsBandwidth) return true
        if (duration > investmentPeriodDays + maxDaysAfter) return true

        return false
    }
}