package com.snowgemm.glad.design.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass

enum class GLADGlassMaterial {
    Regular,
    Clear,
    Tinted,
    HighContrast,
}

object GLADGlassPalette {
    val edge = Color.White.copy(alpha = 0.22f)
    val regularFill = Color.White.copy(alpha = 0.075f)
    val clearFill = Color.White.copy(alpha = 0.045f)
    val tintedFill = Color(0xFF27343A).copy(alpha = 0.36f)
    val highContrastFill = Color(0xFF17191D)
    val neutralTint = Color.White.copy(alpha = 0.07f)
    val tintedTint = Color(0xFFB4D8CF).copy(alpha = 0.10f)
}

/**
 * GL-AD-owned material boundary. Feature screens depend on this component rather than Haze APIs.
 *
 * The translucent fill and edge remain visible if native backdrop rendering is unavailable.
 * Content never relies on refraction or blur to communicate state.
 */
@Composable
fun GLADGlassSurface(
    modifier: Modifier = Modifier,
    material: GLADGlassMaterial = GLADGlassMaterial.Regular,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    input: HazeInput = HazeInput.Backdrop(),
    style: GlassStyle? = null,
    content: @Composable () -> Unit,
) {
    val resolvedStyle = remember(material, shape, style) {
        style ?: when (material) {
            GLADGlassMaterial.Regular -> GlassStyle.regular.then {
                shape(shape)
                backgroundColor(GLADGlassPalette.regularFill)
                tint(GLADGlassPalette.neutralTint)
                specularIntensity(0.48f)
                ambientResponse(0.16f)
            }

            GLADGlassMaterial.Clear -> GlassStyle.clear.then {
                shape(shape)
                backgroundColor(GLADGlassPalette.clearFill)
                tint(Color.White.copy(alpha = 0.035f))
            }

            GLADGlassMaterial.Tinted -> GlassStyle.regular.then {
                shape(shape)
                backgroundColor(GLADGlassPalette.tintedFill)
                tint(GLADGlassPalette.tintedTint)
                specularIntensity(0.50f)
                ambientResponse(0.18f)
            }

            GLADGlassMaterial.HighContrast -> null
        }
    }

    val fallbackFill = when (material) {
        GLADGlassMaterial.Regular -> GLADGlassPalette.regularFill
        GLADGlassMaterial.Clear -> GLADGlassPalette.clearFill
        GLADGlassMaterial.Tinted -> GLADGlassPalette.tintedFill
        GLADGlassMaterial.HighContrast -> GLADGlassPalette.highContrastFill
    }
    val edgeColor = if (material == GLADGlassMaterial.HighContrast) {
        Color.White.copy(alpha = 0.34f)
    } else {
        GLADGlassPalette.edge
    }

    val surfaceModifier = modifier
        .clip(shape)
        .background(fallbackFill)
        .then(
            if (resolvedStyle != null) {
                Modifier.hazeGlass(
                    input = input,
                    style = resolvedStyle,
                    performanceMode = HazePerformanceMode.Default,
                )
            } else {
                Modifier
            },
        )
        .border(1.dp, edgeColor, shape)

    Box(modifier = surfaceModifier) {
        content()
    }
}
