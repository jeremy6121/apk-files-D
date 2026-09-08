package com.example.cosmic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosmic.model.Astronaut
import com.example.cosmic.model.CrewRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrewScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val roster = viewModel.crewSystem.roster
    val credits by viewModel.credits.collectAsState()
    val assignedIds by viewModel.assignedCrewIds.collectAsState()
    val activeRocket by viewModel.activeRocket.collectAsState()

    var showRecruitDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ASTRONAUT ACADEMY & ROSTER",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${assignedIds.size} / ${activeRocket.crewCapacity} Assigned to ${activeRocket.name}",
                            fontSize = 12.sp,
                            color = Color(0xFF29B6F6)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("crew_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Surface(
                        color = Color(0xFF263238),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Credits",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$credits C",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF10141C)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.playClickSound()
                    showRecruitDialog = true
                },
                containerColor = Color(0xFF0288D1),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Recruit Astronaut (800 C)") },
                modifier = Modifier.testTag("recruit_astronaut_fab")
            )
        },
        containerColor = Color(0xFF0C0E14)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Surface(
                color = Color(0xFF161B26),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Active Launch Vehicle: ${activeRocket.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (activeRocket.crewCapacity > 0)
                                "Available Cabin Capacity: ${activeRocket.crewCapacity} crew seats"
                            else
                                "Uncrewed rocket: Add a Command Capsule or Habitat to assign astronauts!",
                            fontSize = 12.sp,
                            color = if (activeRocket.crewCapacity > 0) Color(0xFF00E676) else Color(0xFFFF9800)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(roster) { astronaut ->
                    val isAssigned = assignedIds.contains(astronaut.id)
                    AstronautCard(
                        astronaut = astronaut,
                        isAssigned = isAssigned,
                        onToggleAssign = {
                            viewModel.toggleAssignCrew(astronaut.id)
                        }
                    )
                }
            }
        }
    }

    if (showRecruitDialog) {
        RecruitAstronautDialog(
            credits = credits,
            onDismiss = { showRecruitDialog = false },
            onRecruit = { name, role ->
                viewModel.recruitAstronaut(name, role)
                showRecruitDialog = false
            }
        )
    }
}

@Composable
fun AstronautCard(
    astronaut: Astronaut,
    isAssigned: Boolean,
    onToggleAssign: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141924)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isAssigned) Color(0xFF00E676) else Color(0xFF263238),
                RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                when (astronaut.role) {
                                    CrewRole.COMMANDER -> Color(0xFFFFD700).copy(alpha = 0.2f)
                                    CrewRole.PILOT -> Color(0xFF0288D1).copy(alpha = 0.2f)
                                    CrewRole.ENGINEER -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                    CrewRole.SCIENTIST -> Color(0xFF9C27B0).copy(alpha = 0.2f)
                                    CrewRole.SPECIALIST -> Color(0xFF00E676).copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (astronaut.role) {
                                CrewRole.COMMANDER -> Icons.Default.Stars
                                CrewRole.PILOT -> Icons.Default.Flight
                                CrewRole.ENGINEER -> Icons.Default.Build
                                CrewRole.SCIENTIST -> Icons.Default.Science
                                CrewRole.SPECIALIST -> Icons.Default.Shield
                            },
                            contentDescription = null,
                            tint = when (astronaut.role) {
                                CrewRole.COMMANDER -> Color(0xFFFFD700)
                                CrewRole.PILOT -> Color(0xFF4FC3F7)
                                CrewRole.ENGINEER -> Color(0xFFFFB74D)
                                CrewRole.SCIENTIST -> Color(0xFFBA68C8)
                                CrewRole.SPECIALIST -> Color(0xFF00E676)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = astronaut.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${astronaut.role.title} • Level ${astronaut.experienceLevel} (${astronaut.missionsCompleted} Missions)",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                }

                Button(
                    onClick = onToggleAssign,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAssigned) Color(0xFF00C853) else Color(0xFF263238)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (isAssigned) "ASSIGNED" else "ASSIGN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xFF10141C),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = astronaut.role.specialPerk,
                        fontSize = 11.sp,
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
        }
    }
}

@Composable
fun RecruitAstronautDialog(
    credits: Int,
    onDismiss: () -> Unit,
    onRecruit: (String, CrewRole) -> Unit
) {
    val sampleNames = listOf("Alex Vance", "Elena Rostova", "Marcus Brody", "Maya Lin", "Kenji Sato", "Sarah Connor", "Orion Pax")
    var selectedName by remember { mutableStateOf(sampleNames.random()) }
    var selectedRole by remember { mutableStateOf(CrewRole.PILOT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF181E2C),
        title = {
            Text("Recruit New Astronaut", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Candidate Name:",
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
                OutlinedTextField(
                    value = selectedName,
                    onValueChange = { selectedName = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF0288D1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Specialization Role:",
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(CrewRole.PILOT, CrewRole.ENGINEER, CrewRole.SCIENTIST).forEach { role ->
                        FilterChip(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role },
                            label = { Text(role.title, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0288D1),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Text(
                    text = "Recruitment Fee: 800 C (Current: $credits C)",
                    fontSize = 12.sp,
                    color = Color(0xFFFFD700)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onRecruit(selectedName, selectedRole) },
                enabled = credits >= 800,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
            ) {
                Text("Recruit (800 C)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
