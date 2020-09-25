/**
 * OpenAPI
 * tinkoff.ru/invest OpenAPI.
 *
 * OpenAPI spec version: 1.0.0
 * Contact: n.v.melnikov@tinkoff.ru
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package io.swagger.client.models

/**
 *
 * @param figi
 * @param interval
 * @param o
 * @param c
 * @param h
 * @param l
 * @param v
 * @param time ISO8601
 */
data class Candle(
    val figi: kotlin.String,
    val interval: CandleResolution,
    val o: kotlin.Double,
    val c: kotlin.Double,
    val h: kotlin.Double,
    val l: kotlin.Double,
    val v: kotlin.Int,
    /* ISO8601 */
    val time: java.time.LocalDateTime

) {
}