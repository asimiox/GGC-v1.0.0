package com.example.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserProfileManager
import com.example.ui.screens.auth.AdminAuthContent
import com.example.ui.screens.auth.BsAuthContent
import com.example.ui.screens.auth.FacultyAuthContent
import com.example.ui.screens.auth.IntermediateAuthContent
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.AdminPanelSettings

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDark = Color(0xFF030D2B)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)
private val BrandIconBadgeBg = Color(0xFFEEF3FF)

enum class OnboardingStep {
    WELCOME,
    CONTINUE_AS,
    CHOOSE_LEVEL,
    INTERMEDIATE_AUTH,
    BS_PROGRAMS,
    SELECT_SEMESTER,
    BS_AUTH,
    TEACHER_AUTH,
    HOD_AUTH,
    ADMIN_AUTH
}

data class OnboardingProgramItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

private val bsList = listOf(
    OnboardingProgramItem("BS Information Technology", "Department of IT", Icons.Default.Computer),
    OnboardingProgramItem("BS Business Administration", "Department of BBA", Icons.Default.Business),
    OnboardingProgramItem("BS English", "Department of English", Icons.Default.AutoStories),
    OnboardingProgramItem("BS Islamic Studies", "Department of Islamic Studies", Icons.AutoMirrored.Filled.MenuBook),
    OnboardingProgramItem("BS Physics", "Department of Physics", Icons.Default.Science),
    OnboardingProgramItem("BS Mathematics", "Department of Mathematics", Icons.Default.Calculate),
    OnboardingProgramItem("BS Political Science", "Department of Political Science", Icons.Default.AccountBalance),
    OnboardingProgramItem("BS Urdu", "Department of Urdu", Icons.Default.Translate),
    OnboardingProgramItem("BS Chemistry", "Department of Chemistry", Icons.Default.Science),
    OnboardingProgramItem("BS Zoology", "Department of Zoology", Icons.Default.Pets)
)

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(OnboardingStep.WELCOME) }

    var studentName by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("BS") } // "Intermediate" or "BS"
    var selectedProgram by remember { mutableStateOf("") }
    var selectedSemester by remember { mutableStateOf<String?>("Semester 1") }

    BackHandler(enabled = currentStep != OnboardingStep.WELCOME) {
        currentStep = when (currentStep) {
            OnboardingStep.WELCOME -> OnboardingStep.WELCOME
            OnboardingStep.CONTINUE_AS -> OnboardingStep.WELCOME
            OnboardingStep.CHOOSE_LEVEL -> OnboardingStep.CONTINUE_AS
            OnboardingStep.INTERMEDIATE_AUTH -> OnboardingStep.CHOOSE_LEVEL
            OnboardingStep.BS_PROGRAMS -> OnboardingStep.CHOOSE_LEVEL
            OnboardingStep.SELECT_SEMESTER -> OnboardingStep.BS_PROGRAMS
            OnboardingStep.BS_AUTH -> OnboardingStep.SELECT_SEMESTER
            OnboardingStep.TEACHER_AUTH -> OnboardingStep.CONTINUE_AS
            OnboardingStep.HOD_AUTH -> OnboardingStep.CONTINUE_AS
            OnboardingStep.ADMIN_AUTH -> OnboardingStep.CONTINUE_AS
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("onboarding_screen_container")
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    (slideInHorizontally { width -> width } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                }
            },
            label = "onboarding_step_flow"
        ) { step ->
            when (step) {
                OnboardingStep.WELCOME -> WelcomeStepScreen(
                    onGetStarted = { currentStep = OnboardingStep.CONTINUE_AS }
                )

                OnboardingStep.CONTINUE_AS -> ContinueAsStepScreen(
                    onBack = { currentStep = OnboardingStep.WELCOME },
                    onSelectStudent = {
                        currentStep = OnboardingStep.CHOOSE_LEVEL
                    },
                    onSelectTeacher = {
                        currentStep = OnboardingStep.TEACHER_AUTH
                    }
                )

                OnboardingStep.CHOOSE_LEVEL -> ChooseLevelStepScreen(
                    onBack = { currentStep = OnboardingStep.CONTINUE_AS },
                    onSelectLevel = { level ->
                        selectedLevel = level
                        if (level == "Intermediate") {
                            currentStep = OnboardingStep.INTERMEDIATE_AUTH
                        } else {
                            currentStep = OnboardingStep.BS_PROGRAMS
                        }
                    }
                )

                OnboardingStep.INTERMEDIATE_AUTH -> IntermediateAuthStepScreen(
                    initialName = studentName,
                    onBack = { currentStep = OnboardingStep.CHOOSE_LEVEL },
                    onAuthSuccess = onOnboardingFinished
                )

                OnboardingStep.BS_PROGRAMS -> BsProgramsStepScreen(
                    onBack = { currentStep = OnboardingStep.CHOOSE_LEVEL },
                    onSelectProgram = { progName ->
                        selectedProgram = progName
                        currentStep = OnboardingStep.SELECT_SEMESTER
                    }
                )

                OnboardingStep.SELECT_SEMESTER -> SelectSemesterStepScreen(
                    selectedSemester = selectedSemester,
                    onSelectSemester = { selectedSemester = it },
                    onBack = { currentStep = OnboardingStep.BS_PROGRAMS },
                    onContinue = {
                        currentStep = OnboardingStep.BS_AUTH
                    }
                )

                OnboardingStep.BS_AUTH -> BsAuthStepScreen(
                    initialProgram = selectedProgram,
                    initialSemester = selectedSemester,
                    onBack = { currentStep = OnboardingStep.SELECT_SEMESTER },
                    onAuthSuccess = onOnboardingFinished
                )

                OnboardingStep.TEACHER_AUTH -> TeacherAuthStepScreen(
                    onBack = { currentStep = OnboardingStep.CONTINUE_AS },
                    onAuthSuccess = onOnboardingFinished
                )

                OnboardingStep.HOD_AUTH -> HodAuthStepScreen(
                    onBack = { currentStep = OnboardingStep.CONTINUE_AS },
                    onAuthSuccess = onOnboardingFinished
                )

                OnboardingStep.ADMIN_AUTH -> AdminAuthStepScreen(
                    onBack = { currentStep = OnboardingStep.CONTINUE_AS },
                    onAuthSuccess = onOnboardingFinished
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: WELCOME SCREEN (Reference Image: Screen 2)
// -------------------------------------------------------------
@Composable
private fun WelcomeStepScreen(
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))

            // App Identity Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ggc_logo),
                    contentDescription = "GGC Logo",
                    modifier = Modifier.size(38.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "GGC M.B.Din",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Text(
                        text = "Official App",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Welcome",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Education · Excellence · Integrity",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = BrandTextMuted
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Hero Image with Soft Rounded Corners
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = BrandBackground)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_01),
                    contentDescription = "Govt Graduate College Mandi Bahauddin Campus",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // CTA Button
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("welcome_get_started_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: CONTINUE AS (Role Selection)
// -------------------------------------------------------------
@Composable
private fun ContinueAsStepScreen(
    onBack: () -> Unit,
    onSelectStudent: () -> Unit,
    onSelectTeacher: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("continue_as_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BrandNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Choose Login Portal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select your designated official portal to sign in",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = BrandTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Portal Option 1: Student (BS / Intermediate)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectStudent() }
                    .testTag("role_option_student"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFC59B27).copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Student Portal",
                            tint = Color(0xFFE5C058),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Student Portal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "BS Honors (4-Year) & Intermediate",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Academics · Outlines · Roll No Login",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE5C058)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFE5C058),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Portal Option 2: Teacher & Staff (Faculty / HOD / Admin)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectTeacher() }
                    .testTag("role_option_teacher"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C245E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFC59B27).copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Teacher & Staff Portal",
                            tint = Color(0xFFE5C058),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Teacher & Staff Portal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Teaching Faculty · HODs · Administration",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Courses · Notes · Department Command",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE5C058)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFE5C058),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Bottom college footer notice
        Text(
            text = "Official Academic App · Govt. Graduate College Mandi Bahauddin",
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = BrandTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )
    }
}

// -------------------------------------------------------------
// HOD AUTH STEP (Head of Department Portal)
// -------------------------------------------------------------
@Composable
private fun HodAuthStepScreen(
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedDepartment by remember { mutableStateOf(com.example.data.model.GgcOfficialDepartments.LIST.first()) }
    var hodId by remember { mutableStateOf(com.example.data.model.GgcOfficialDepartments.generateDefaultHodId(selectedDepartment)) }
    var password by remember { mutableStateOf("00000") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val departments = com.example.data.model.GgcOfficialDepartments.LIST

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("hod_auth_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "HOD Command Center",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Head of Department Access & Governance",
                    fontSize = 12.sp,
                    color = BrandTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hod_auth_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0C245E).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = Color(0xFF0C245E),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Department Leadership",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "Manage faculty, import student lists & broadcast notices",
                            fontSize = 11.sp,
                            color = BrandTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Department",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Department Selector Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(departments) { dept ->
                        val isSelected = selectedDepartment == dept
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDepartment = dept
                                hodId = com.example.data.model.GgcOfficialDepartments.generateDefaultHodId(dept)
                            },
                            label = { Text(dept, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandNavy,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = hodId,
                    onValueChange = { hodId = it },
                    label = { Text("HOD Faculty ID / Username") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BrandNavy) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = Color(0xFFDCE2EE)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BrandNavy) },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = Color(0xFFDCE2EE)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        UserProfileManager.saveHodProfile(
                            context = context,
                            fullName = "Prof. Dr. Head of Department ($selectedDepartment)",
                            department = selectedDepartment,
                            hodId = hodId
                        )
                        onAuthSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("hod_login_submit_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    Text(
                        text = "Enter HOD Command Center",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TEACHER AUTH STEP (Official Supabase Faculty Verification & Login)
// -------------------------------------------------------------
@Composable
private fun TeacherAuthStepScreen(
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("teacher_auth_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "Faculty Portal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Official Teacher Login & Claim",
                    fontSize = 12.sp,
                    color = BrandTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Embedded Supabase Faculty Verification and Authentication
        FacultyAuthContent(
            onAuthSuccess = onAuthSuccess,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// -------------------------------------------------------------
// ADMIN AUTH STEP (Official Supabase Super Control Login)
// -------------------------------------------------------------
@Composable
private fun AdminAuthStepScreen(
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    AdminAuthContent(
        onBack = onBack,
        onAuthSuccess = onAuthSuccess
    )
}

// -------------------------------------------------------------
// STEP 3: CHOOSE PROGRAM LEVEL (Reference Image: Screen 4)
// -------------------------------------------------------------
@Composable
private fun ChooseLevelStepScreen(
    onBack: () -> Unit,
    onSelectLevel: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("level_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Choose Program Level",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Select your current student level",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = BrandTextMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Level Option 1: Intermediate Student
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onSelectLevel("Intermediate") }
                .testTag("level_card_intermediate"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandIconBadgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Intermediate Student",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Verified Portal Login & Registration (2 Years)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = BrandTextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Level Option 2: BS Student
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onSelectLevel("BS") }
                .testTag("level_card_bs"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandIconBadgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "BS Student",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "4 Years BS Degree Programs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = BrandTextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 4A: INTERMEDIATE AUTHENTICATION (Login / Registration)
// -------------------------------------------------------------
@Composable
private fun IntermediateAuthStepScreen(
    initialName: String,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("intermediate_auth_step_container")
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("inter_auth_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Intermediate Registration",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Intermediate Auth Card
        IntermediateAuthContent(
            onAuthSuccess = onAuthSuccess
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// STEP 4B: BS PROGRAMS (Reference Image: Screen 6 - Unchanged)
// -------------------------------------------------------------
@Composable
private fun BsProgramsStepScreen(
    onBack: () -> Unit,
    onSelectProgram: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("bs_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "BS Programs",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Select your program",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = BrandTextMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(bsList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectProgram(item.title) }
                        .testTag("bs_item_${item.title.replace(" ", "_").lowercase()}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrandIconBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = item.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = BrandNavy,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = BrandTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 5: SELECT SEMESTER (Reference Image: Screen 7 - Unchanged)
// -------------------------------------------------------------
@Composable
private fun SelectSemesterStepScreen(
    selectedSemester: String?,
    onSelectSemester: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val semesters = (1..8).map { "Semester $it" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("semester_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BrandNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Select Semester",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Choose your current semester",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = BrandTextMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 2-Column Grid of 8 Semester Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(semesters) { semester ->
                    val isSelected = selectedSemester == semester
                    val num = semester.removePrefix("Semester ")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectSemester(semester) }
                            .testTag("sem_card_$num"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandNavy else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = num,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else BrandNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = semester,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else BrandTextMuted
                            )
                        }
                    }
                }
            }
        }

        // Continue Button
        Button(
            onClick = onContinue,
            enabled = selectedSemester != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("semester_continue_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandNavy,
                disabledContainerColor = BrandNavy.copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 6: BS AUTHENTICATION (Login / Registration Portal)
// -------------------------------------------------------------
@Composable
private fun BsAuthStepScreen(
    initialProgram: String,
    initialSemester: String?,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("bs_auth_step_container")
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("bs_auth_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "BS Student Registration",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BS Auth Card
        BsAuthContent(
            initialProgram = initialProgram,
            initialSemester = initialSemester,
            onAuthSuccess = onAuthSuccess
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
