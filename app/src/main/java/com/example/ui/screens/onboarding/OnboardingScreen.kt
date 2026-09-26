package com.example.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.UserProfileManager
import com.example.ui.screens.auth.AdminAuthContent
import com.example.ui.screens.auth.BsAuthContent
import com.example.ui.screens.auth.FacultyAuthContent
import com.example.ui.screens.auth.IntermediateAuthContent
import kotlinx.coroutines.launch

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC5A059)
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

private data class OnboardingPageData(
    val title: String,
    val description: String,
    val imageRes: Int,
    val isOfficialLogo: Boolean = false
)

private val onboardingPages = listOf(
    OnboardingPageData(
        title = "Welcome to GGC M.B.Din",
        description = "Your official college app for academics, announcements and campus information.",
        imageRes = R.drawable.img_hero_01,
        isOfficialLogo = false
    ),
    OnboardingPageData(
        title = "Everything You Need for Your Studies",
        description = "Explore programs, courses, faculty information and academic resources in one place.",
        imageRes = R.drawable.img_onboarding_academics,
        isOfficialLogo = false
    ),
    OnboardingPageData(
        title = "Stay Connected With Your College",
        description = "Keep up with official announcements, notices, events and important college updates.",
        imageRes = R.drawable.img_onboarding_updates,
        isOfficialLogo = false
    ),
    OnboardingPageData(
        title = "Your College Experience, Organized",
        description = "Keep your academic information and student services together in one simple place.",
        imageRes = R.drawable.img_onboarding_student,
        isOfficialLogo = false
    ),
    OnboardingPageData(
        title = "GGC M.B.Din Official App",
        description = "Your official digital connection to Government Graduate College Mandi Bahauddin.",
        imageRes = R.drawable.ic_ggc_logo,
        isOfficialLogo = true
    )
)

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    var currentStep by remember { mutableStateOf(OnboardingStep.WELCOME) }

    var studentName by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("BS") } // "Intermediate" or "BS"
    var selectedProgram by remember { mutableStateOf("") }
    var selectedSemester by remember { mutableStateOf<String?>("Semester 1") }

    BackHandler(enabled = currentStep != OnboardingStep.WELCOME || pagerState.currentPage > 0) {
        if (currentStep == OnboardingStep.WELCOME) {
            if (pagerState.currentPage > 0) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            }
        } else {
            currentStep = when (currentStep) {
                OnboardingStep.WELCOME -> OnboardingStep.WELCOME
                OnboardingStep.CONTINUE_AS -> OnboardingStep.WELCOME
                OnboardingStep.CHOOSE_LEVEL -> OnboardingStep.CONTINUE_AS
                OnboardingStep.INTERMEDIATE_AUTH -> OnboardingStep.CHOOSE_LEVEL
                OnboardingStep.BS_PROGRAMS -> OnboardingStep.CHOOSE_LEVEL
                OnboardingStep.SELECT_SEMESTER -> OnboardingStep.BS_AUTH
                OnboardingStep.BS_AUTH -> OnboardingStep.CHOOSE_LEVEL
                OnboardingStep.TEACHER_AUTH -> OnboardingStep.CONTINUE_AS
                OnboardingStep.HOD_AUTH -> OnboardingStep.CONTINUE_AS
                OnboardingStep.ADMIN_AUTH -> OnboardingStep.CONTINUE_AS
            }
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
                OnboardingStep.WELCOME -> ModernOnboardingWalkthroughScreen(
                    pagerState = pagerState,
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
                        if (level == "BS") {
                            currentStep = OnboardingStep.BS_AUTH
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
                    onBack = { currentStep = OnboardingStep.BS_AUTH },
                    onContinue = {
                        selectedSemester?.let { sem ->
                            UserProfileManager.updateSemester(context, sem)
                        }
                        onOnboardingFinished()
                    }
                )

                OnboardingStep.BS_AUTH -> BsAuthStepScreen(
                    initialProgram = null,
                    initialSemester = null,
                    onBack = { currentStep = OnboardingStep.CHOOSE_LEVEL },
                    onAuthSuccess = {
                        // After successful login, ask for active semester!
                        currentStep = OnboardingStep.SELECT_SEMESTER
                    }
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
// STEP 1: MODERN ONBOARDING WALKTHROUGH (5-PAGE VISUAL STORYTELLING)
// -------------------------------------------------------------
@Composable
private fun ModernOnboardingWalkthroughScreen(
    pagerState: PagerState,
    onGetStarted: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isFinalPage = pagerState.currentPage == onboardingPages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP NAVIGATION BAR (Back / Identity + Skip)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage > 0) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("onboarding_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Page",
                        tint = BrandNavy
                    )
                }
            } else {
                // College Mini Identity Badge on Page 1
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ggc_logo),
                        contentDescription = "GGC Official Logo",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GGC M.B.Din",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                }
            }

            if (!isFinalPage) {
                TextButton(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("onboarding_skip_btn")
                ) {
                    Text(
                        text = "Skip",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(44.dp))
            }
        }

        // HORIZONTAL PAGER: HERO VISUAL + HEADING + SUBTITLE
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = onboardingPages[pageIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Hero Visual Area (~50% height with generous whitespace)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (page.isOfficialLogo) {
                        // FINAL PAGE: Official GGC Seal Crest Presentation
                        Surface(
                            modifier = Modifier
                                .size(240.dp)
                                .testTag("onboarding_final_seal"),
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(2.dp, BrandGold.copy(alpha = 0.5f)),
                            shadowElevation = 3.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFFAFBFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Concentric Inner Ring
                                Box(
                                    modifier = Modifier
                                        .size(196.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_ggc_logo),
                                        contentDescription = "GGC M.B.Din Official Seal",
                                        modifier = Modifier
                                            .size(140.dp)
                                            .testTag("onboarding_official_logo")
                                    )
                                }
                            }
                        }
                    } else {
                        // Large Rounded Hero Visual Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 220.dp, max = 290.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE9EDF5)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Image(
                                painter = painterResource(id = page.imageRes),
                                contentDescription = page.title,
                                contentScale = if (page.imageRes == R.drawable.img_hero_01) ContentScale.Crop else ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(if (page.imageRes == R.drawable.img_hero_01) 0.dp else 16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Short Strong Heading
                Text(
                    text = page.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // One Concise Supporting Sentence
                Text(
                    text = page.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = BrandTextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // BOTTOM CONTROLS & PAGINATION
        if (!isFinalPage) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated Page Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("onboarding_page_indicator")
                ) {
                    for (i in onboardingPages.indices) {
                        val isSelected = pagerState.currentPage == i
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 26.dp else 7.dp,
                            animationSpec = tween(durationMillis = 280),
                            label = "indicator_width"
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) BrandNavy else Color(0xFFCBD5E1),
                            animationSpec = tween(durationMillis = 280),
                            label = "indicator_color"
                        )
                        Box(
                            modifier = Modifier
                                .height(7.dp)
                                .width(width)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                        )
                    }
                }

                // Modern Circular Next Button
                Surface(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                        .testTag("onboarding_next_btn"),
                    shape = CircleShape,
                    color = BrandNavy,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Page",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        } else {
            // FINAL PAGE: Indicator + Large Primary CTA "Get Started"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, top = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Centered Animated Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 18.dp)
                        .testTag("onboarding_page_indicator_final")
                ) {
                    for (i in onboardingPages.indices) {
                        val isSelected = pagerState.currentPage == i
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 26.dp else 7.dp,
                            animationSpec = tween(durationMillis = 280),
                            label = "final_indicator_width"
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) BrandNavy else Color(0xFFCBD5E1),
                            animationSpec = tween(durationMillis = 280),
                            label = "final_indicator_color"
                        )
                        Box(
                            modifier = Modifier
                                .height(7.dp)
                                .width(width)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                        )
                    }
                }

                // Get Started Button
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("welcome_get_started_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Get Started",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
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

                Spacer(modifier = Modifier.height(6.dp))

                // Secondary Portal Access Link
                TextButton(
                    onClick = onGetStarted,
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = "Sign in to Student or Faculty Portal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandTextMuted
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: CONTINUE AS (Role Selection / Choose Login Portal)
// -------------------------------------------------------------
@Composable
private fun ContinueAsStepScreen(
    onBack: () -> Unit,
    onSelectStudent: () -> Unit,
    onSelectTeacher: () -> Unit
) {
    val portalCardBorder = Color(0xFFE9EDF5)
    val portalIconBg = Color(0xFFEEF2F8)
    val portalArrowGold = Color(0xFFC59B27)
    val portalTextSubtitle = Color(0xFF7A879D)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // HEADER BAR: Navy Back Arrow + Center-aligned Official Logo & App Name
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 28.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp)
                        .testTag("continue_as_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BrandNavy
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ggc_logo),
                        contentDescription = "GGC Logo",
                        modifier = Modifier.size(38.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "GGC M.B.Din",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = "Official App",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = portalTextSubtitle,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // PAGE TITLE & SUBTITLE
            Text(
                text = "Choose Login Portal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select your designated official portal to sign in",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = portalTextSubtitle,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // CARD 1: Student Portal
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectStudent() }
                    .testTag("role_option_student"),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, portalCardBorder),
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular icon container
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(portalIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Student Portal",
                            tint = BrandNavy,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Student Portal",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "BS Honors (4-Year) &\nIntermediate",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = portalTextSubtitle,
                            lineHeight = 17.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = portalArrowGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CARD 2: Teacher & Staff Portal
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectTeacher() }
                    .testTag("role_option_teacher"),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, portalCardBorder),
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular icon container
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(portalIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Teacher & Staff Portal",
                            tint = BrandNavy,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Teacher & Staff Portal",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Teaching Faculty · HODs ·\nAdministration",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = portalTextSubtitle,
                            lineHeight = 17.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = portalArrowGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // SUBTLE DIVIDER MESSAGE: "Choose the portal that matches your role."
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(1.dp)
                        .background(Color(0xFFDDE3EC))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Choose the portal that matches your role.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = portalTextSubtitle
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(1.dp)
                        .background(Color(0xFFDDE3EC))
                )
            }
        }

        // BOTTOM FOOTER: "GGC M.B.Din · Official App"
        Text(
            text = "GGC M.B.Din · Official App",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = portalTextSubtitle,
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
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        FacultyAuthContent(
            onBack = onBack,
            onAuthSuccess = onAuthSuccess,
            modifier = Modifier.fillMaxSize()
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
    var showIntermediateMaintenanceDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
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

        // Level Option 1: Intermediate Student (Locked)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    showIntermediateMaintenanceDialog = true
                }
                .testTag("level_card_intermediate"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
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
                        .background(Color(0xFFFEF2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Intermediate Locked",
                        tint = Color(0xFFDC2626),
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
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Higher Secondary Programs (FA / FSc / ICS / I.Com)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(16.dp)
                    )
                }
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

    // Modal dialog stopping user when Intermediate is clicked
    if (showIntermediateMaintenanceDialog) {
        Dialog(
            onDismissRequest = { showIntermediateMaintenanceDialog = false },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Under Maintenance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Intermediate Student Portal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFDC2626),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF9FAFB),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "The Intermediate Student Portal is currently undergoing scheduled maintenance. All logins, admissions, and student registrations are temporarily paused.",
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showIntermediateMaintenanceDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                    ) {
                        Text("Understood", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
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
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("intermediate_auth_step_container")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
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
                text = "Intermediate Student Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Intermediate Under Maintenance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFFCC80).copy(alpha = 0.7f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Maintenance",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Under Maintenance",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Intermediate Student Portal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE65100),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF8E1),
                    border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "The Intermediate Student Portal is currently locked for maintenance. All logins and student services for Intermediate programs are temporarily paused.",
                        fontSize = 12.sp,
                        color = Color(0xFF8D6E63),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Please check back later or visit the College Administration office for assistance.",
                    fontSize = 12.sp,
                    color = BrandTextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    Text("Return to Program Selection", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

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
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
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
// STEP 5: SELECT SEMESTER (Post-Login Active Semester Selector)
// -------------------------------------------------------------
@Composable
private fun SelectSemesterStepScreen(
    selectedSemester: String?,
    onSelectSemester: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val userProfile = UserProfileManager.userProfile.value
    val semesters = (1..8).map { "Semester $it" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Welcome, ${userProfile.name.ifBlank { "Student" }}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Official Student Identity Card (Department Locked & Verified)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Official Assigned Department",
                            fontSize = 11.sp,
                            color = Color(0xFFC59B27),
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LOCKED RECORD",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userProfile.programName.ifBlank { "BS Information Technology" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (!userProfile.rollNumber.isNullOrBlank() || !userProfile.registrationNumber.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!userProfile.rollNumber.isNullOrBlank()) {
                                Text(
                                    text = "Roll: ${userProfile.rollNumber}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            if (!userProfile.registrationNumber.isNullOrBlank()) {
                                Text(
                                    text = "•  Reg: ${userProfile.registrationNumber}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Active Semester",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Choose your current active semester to access the syllabus, timetable, and study materials.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = BrandTextMuted,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2-Column Grid of 8 Semester Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(semesters) { semester ->
                    val isSelected = selectedSemester == semester
                    val num = semester.removePrefix("Semester ")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectSemester(semester) }
                            .testTag("sem_card_$num"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandNavy else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = num,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else BrandNavy
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = semester,
                                fontSize = 11.sp,
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
                .padding(bottom = 16.dp, top = 8.dp)
                .height(52.dp)
                .testTag("semester_continue_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandNavy,
                disabledContainerColor = BrandNavy.copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Confirm Semester & Enter Dashboard",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
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
    initialProgram: String? = null,
    initialSemester: String? = null,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("bs_auth_step_container")
    ) {
        BsAuthContent(
            initialProgram = initialProgram,
            initialSemester = initialSemester,
            onBack = onBack,
            onAuthSuccess = onAuthSuccess,
            modifier = Modifier.fillMaxSize()
        )
    }
}
