package com.stockpro.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class ProductSkuTest {
    private val fixedTime = LocalDateTime.of(2026, 10, 27, 17, 34, 22)

    @Test fun generatedSkuHasReadablePrefixTimestampAndSuffix() {
        val sku = ProductSku.generate(fixedTime, "a1b2-c3d4")
        assertEquals("SP-261027-173422-A1B2", sku)
    }

    @Test fun generatedSkuNormalizesToUppercaseAndTrimsWhitespace() {
        assertEquals("SP-ABC-123", ProductSku.normalize("  sp-abc-123 "))
    }

    @Test fun distinctTokensProduceDistinctSkusAtSameTime() {
        val first = ProductSku.generate(fixedTime, "1111-aaaa")
        val second = ProductSku.generate(fixedTime, "2222-bbbb")
        assertNotEquals(first, second)
    }

    @Test fun generatedSkuIsUppercaseAndStartsWithStockProPrefix() {
        val sku = ProductSku.generate(fixedTime, "abcd-1234")
        assertTrue(sku.startsWith("SP-"))
        assertEquals(sku, sku.uppercase())
    }
}
