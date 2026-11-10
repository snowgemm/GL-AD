package com.snowgemm.glad.design.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlassContainer

@Composable
fun GLADGlassNavigation(
    selectedIndex: Int,
    labels: List<String>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val memberShape = remember { RoundedCornerShape(22.dp) }
    val memberStyle = remember(memberShape) {
        GlassStyle.regular.then {
            shape(memberShape)
            backgroundColor(GLADGlassPalette.regularFill)
            tint(GLADGlassPalette.neutralTint)
            specularIntensity(0.48f)
            ambientResponse(0.16f)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .hazeGlassContainer(mergeDistance = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        labels.forEachIndexed { index, label ->
            GLADGlassSurface(
                modifier = Modifier.weight(1f),
                material = GLADGlassMaterial.Regular,
                shape = memberShape,
                input = HazeInput.Backdrop(),
                style = memberStyle,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(memberShape)
                        .background(
                            if (selectedIndex == index) {
                                Color.White.copy(alpha = 0.11f)
                            } else {
                                Color.Transparent
                            },
                        )
                        .clickable { onSelect(index) }
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(
                            alpha = if (selectedIndex == index) 1f else 0.72f,
                        ),
                        fontWeight = if (selectedIndex == index) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        },
                    )
                }
            }
        }
    }
}
