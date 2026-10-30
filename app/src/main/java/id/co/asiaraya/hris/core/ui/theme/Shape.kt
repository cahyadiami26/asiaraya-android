package id.co.asiaraya.hris.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CardShape = RoundedCornerShape(14.dp)
val ButtonShape = RoundedCornerShape(9.dp)
val InputShape = RoundedCornerShape(9.dp)
val BadgeShape = RoundedCornerShape(6.dp)
val DialogShape = RoundedCornerShape(20.dp)
val SheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

val AppShapes = Shapes(
    small = BadgeShape,
    medium = ButtonShape,
    large = CardShape,
    extraLarge = DialogShape
)
