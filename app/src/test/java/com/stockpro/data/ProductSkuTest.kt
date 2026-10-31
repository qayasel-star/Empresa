package com.stockpro.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductSkuTest {
    @Test fun arrozUsesSequentialArrReference() {
        assertEquals("ARR-001", ProductSku.generate("Arroz de 1 kg"))
        assertEquals("ARR-002", ProductSku.generate("Arroz de 1 kg", existingSkus = listOf("ARR-001")))
    }
    @Test fun examplesUseExpectedPrefixes() {
        assertEquals("ACE-001", ProductSku.generate("Aceite de 1 litro"))
        assertEquals("AZU-001", ProductSku.generate("Azúcar de 1 kg"))
        assertEquals("REF-001", ProductSku.generate("Refresco de 2 litros"))
    }
    @Test fun fillsGapsWithoutReusingAnExistingReference() {
        assertEquals("ARR-002", ProductSku.generate("Arroz", existingSkus = listOf("ARR-001", "ARR-003")))
    }
    @Test fun normalizationTrimsAndUppercases() {
        assertEquals("SP-ABC-123", ProductSku.normalize("  sp-abc-123 "))
    }
    @Test fun differentFamiliesHaveDifferentPrefixes() {
        assertNotEquals(ProductSku.generate("Arroz"), ProductSku.generate("Aceite"))
        assertTrue(ProductSku.generate("Arroz").startsWith("ARR-"))
    }
}
