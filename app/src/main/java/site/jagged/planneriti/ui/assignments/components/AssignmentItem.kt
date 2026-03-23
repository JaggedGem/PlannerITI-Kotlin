package site.jagged.planneriti.ui.assignments.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import site.jagged.planneriti.domain.model.Assignment
import site.jagged.planneriti.ui.assignments.color
import site.jagged.planneriti.ui.assignments.dueDateFormatted
import site.jagged.planneriti.ui.assignments.remainingText

@Composable
fun AssignmentItem(
    assignment: Assignment,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (assignment.isCompleted)
            Color(0xFF1A1A1A)
        else
            Color(0xFF1C1C1E),
        animationSpec = tween(150),
        label = "card_color"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    if (assignment.isCompleted) Color(0xFF3478F6)
                    else Color.Transparent
                )
                .then(
                    if (!assignment.isCompleted)
                        Modifier.background(Color.Transparent)
                    else Modifier
                )
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        if (assignment.isCompleted) Color(0xFF3478F6) else Color.Transparent
                    )
                    .then(
                        Modifier.then(
                            if (!assignment.isCompleted)
                                Modifier.background(Color.Transparent)
                                    .border(width = 1.dp, color = Color(0xFF3478F6), shape = CircleShape)
                            else Modifier
                        )
                    )
            )
            // Use a simpler approach
        }

        // Let's use a cleaner checkbox approach
        Spacer(modifier = Modifier.width(4.dp))
    }
}

// A cleaner version:
@Composable
fun AssignmentItemClean(
    assignment: Assignment,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = Color(assignment.assignmentType.color())
    val isOverdue = !assignment.isCompleted && System.currentTimeMillis() > assignment.dueDate

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C1E), RoundedCornerShape(12.dp))
            .then(
                if (isOverdue) Modifier.padding(start = 3.dp)
                else Modifier
            )
            .background(
                if (isOverdue) Color(0xFFFF3B30) else Color.Transparent,
                RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
            )
            .background(Color(0xFF1C1C1E), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Type indicator dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(typeColor)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = assignment.title,
                color = if (assignment.isCompleted) Color(0xFF8A8A8D) else Color.White,
                fontSize = 15.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                textDecoration = if (assignment.isCompleted) TextDecoration.LineThrough else null,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (assignment.courseName.isNotEmpty()) {
                Text(
                    text = assignment.courseName,
                    color = Color(0xFF8A8A8D),
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right side - time and remaining
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = assignment.dueDateFormatted(),
                color = Color(0xFF8A8A8D),
                fontSize = 12.sp
            )
            if (!assignment.isCompleted) {
                Text(
                    text = assignment.remainingText(),
                    color = if (isOverdue) Color(0xFFFF3B30) else Color(0xFF8E8E93),
                    fontSize = 12.sp
                )
            }
        }

        // Checkbox
        Spacer(modifier = Modifier.width(8.dp))
        Checkbox(
            checked = assignment.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF3478F6),
                uncheckedColor = Color(0xFF3478F6),
                checkmarkColor = Color.White
            ),
            modifier = Modifier.size(20.dp)
        )
    }
}