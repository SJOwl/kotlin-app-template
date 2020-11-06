package trading.statisics

import trading.infrastructure.CombinationsProcessor
import trading.infrastructure.SubsetProcessor

class PortfolioFinder3(
    private val strategy: PortfolioProfitsChartStrategy,
    private val k: Int
) : IPortfolioFinder {

    private val processor = PortfolioProfitProcessor()

    override fun findBestSet(candles: List<TickerCandles>): List<TickerCandles> {

        val res = SubsetProcessor().process(
            candles,
            k + 5,
            k
        ) { first: List<TickerCandles>, other: List<TickerCandles> ->

            var sett = first
            var currentProfitsChart = processor.values(sett)
            var counter = 0

            val sum = mutableListOf<TickerCandles>().apply {
                addAll(first)
                addAll(other)
            }

            CombinationsProcessor().process(sum, k) { set ->
                val newSetProfitsChart = processor.values(set)
                if (strategy.isBetter(currentProfitsChart, newSetProfitsChart)) {
                    currentProfitsChart = newSetProfitsChart
                    sett = set
                    println(
                        "new set is better: ${
                            set.sortedBy { it.ticker }.joinToString(",") { "\"${it.ticker}\"" }
                        }"
                    )
                }
                counter++
            }

            sett.sortedBy { it.ticker }
        }

        return res
    }
}