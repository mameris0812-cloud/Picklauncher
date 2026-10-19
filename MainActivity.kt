package com.example.picklauncher

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// ───────────── 색상 ─────────────
private val Orange = Color(0xFFF99E1A)
private val OrangeDark = Color(0xFFD97F00)
private val IceBlue = Color(0xFF9BE3FF)
private val SlotBlue = Color(0xFF3FA9F5)
private val NameBlue = Color(0xFF36B6FF)
private val PanelDark = Color(0xCC0F1624)
private val SlotLine = Color(0xFFB9C6DA)
private val BgTop = Color(0xFF34425E)
private val BgMid = Color(0xFF1C2639)
private val BgBottom = Color(0xFF0B111C)

private const val SLOT_COUNT = 5

// ───────────── 데이터 ─────────────
enum class AppGroup(val label: String, val sub: String) {
    GAME("게임", "GAME"),
    SOCIAL("소셜", "SOCIAL"),
    MEDIA("미디어", "MEDIA"),
    TOOL("도구", "TOOL"),
    ETC("기타", "ETC"),
    SYSTEM("시스템", "SYSTEM")
}

@Immutable
data class AppEntry(
    val label: String,
    val pkg: String,
    val icon: ImageBitmap,
    val group: AppGroup
)

// 육각형 (꼭짓점이 위로)
private val HexShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w / 2f, 0f)
    lineTo(w, h * 0.25f)
    lineTo(w, h * 0.75f)
    lineTo(w / 2f, h)
    lineTo(0f, h * 0.75f)
    lineTo(0f, h * 0.25f)
    close()
}

class MainActivity : ComponentActivity() {
    private var resumeTick by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                BackHandler { /* 홈 화면이므로 뒤로가기 무시 */ }
                LauncherScreen(resumeTick)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeTick++ // 앱 설치/삭제 후 돌아오면 목록 갱신
    }
}

// ───────────── 앱 목록 / 저장 ─────────────
private fun classify(info: ApplicationInfo): AppGroup {
    val isSystem = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    return when (info.category) {
        ApplicationInfo.CATEGORY_GAME -> AppGroup.GAME
        ApplicationInfo.CATEGORY_SOCIAL,
        ApplicationInfo.CATEGORY_NEWS -> AppGroup.SOCIAL
        ApplicationInfo.CATEGORY_AUDIO,
        ApplicationInfo.CATEGORY_VIDEO,
        ApplicationInfo.CATEGORY_IMAGE -> AppGroup.MEDIA
        ApplicationInfo.CATEGORY_PRODUCTIVITY,
        ApplicationInfo.CATEGORY_MAPS,
        ApplicationInfo.CATEGORY_ACCESSIBILITY -> AppGroup.TOOL
        else -> if (isSystem) AppGroup.SYSTEM else AppGroup.ETC
    }
}

private fun loadApps(ctx: Context): List<AppEntry> {
    val pm = ctx.packageManager
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return pm.queryIntentActivities(intent, 0)
        .filter { it.activityInfo.packageName != ctx.packageName }
        .distinctBy { it.activityInfo.packageName }
        .map { ri ->
            AppEntry(
                label = ri.loadLabel(pm).toString(),
                pkg = ri.activityInfo.packageName,
                icon = ri.loadIcon(pm).toBitmap(128, 128).asImageBitmap(),
                group = classify(ri.activityInfo.applicationInfo)
            )
        }
        .sortedWith(
            compareBy<AppEntry>({ it.group.ordinal }, { it.label.lowercase() })
        )
}

private fun loadSlots(prefs: SharedPreferences): List<String> {
    val raw = prefs.getString("slots", "") ?: ""
    val parts = raw.split("|")
    return List(SLOT_COUNT) { parts.getOrNull(it) ?: "" }
}

private fun saveSlots(prefs: SharedPreferences, slots: List<String>) {
    prefs.edit().putString("slots", slots.joinToString("|")).apply()
}

private fun launchApp(ctx: Context, pkg: String) {
    try {
        ctx.packageManager.getLaunchIntentForPackage(pkg)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(it)
        }
    } catch (e: Exception) {
        // 실행 불가 앱은 무시
    }
}

private fun openAppInfo(ctx: Context, pkg: String) {
    try {
        val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$pkg"))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    } catch (e: Exception) {
    }
}

private fun uninstallApp(ctx: Context, pkg: String) {
    try {
        val i = Intent(Intent.ACTION_DELETE, Uri.parse("package:$pkg"))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    } catch (e: Exception) {
    }
}

private fun formatTime(): String = SimpleDateFormat("H:mm", Locale.getDefault()).format(Date())
private fun formatDate(): String = SimpleDateFormat("M월 d일 EEEE", Locale.KOREA).format(Date())

// ───────────── 메인 화면 ─────────────
@Composable
fun LauncherScreen(resumeTick: Int) {
    val ctx = LocalContext.current
    val density = LocalDensity.current
    val prefs = remember { ctx.getSharedPreferences("pick_launcher", Context.MODE_PRIVATE) }

    var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    var slots by remember { mutableStateOf(loadSlots(prefs)) }
    var selectedPkg by remember { mutableStateOf(prefs.getString("last", null)) }
    var scrollTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(resumeTick) {
        apps = withContext(Dispatchers.Default) { loadApps(ctx) }
    }

    val clock by produceState(initialValue = Pair(formatTime(), formatDate())) {
        while (true) {
            value = Pair(formatTime(), formatDate())
            delay(1000L)
        }
    }

    val selected = apps.firstOrNull { it.pkg == selectedPkg } ?: apps.firstOrNull()
    val selectedIndex = apps.indexOfFirst { it.pkg == selected?.pkg }
    val groups = remember(apps) { apps.groupBy { it.group }.toList() }
    val rosterState = rememberLazyListState()

    fun select(pkg: String) {
        selectedPkg = pkg
        prefs.edit().putString("last", pkg).apply()
    }

    fun step(delta: Int) {
        if (apps.isEmpty()) return
        val i = if (selectedIndex < 0) 0 else (selectedIndex + delta + apps.size) % apps.size
        select(apps[i].pkg)
        scrollTick++
    }

    fun updateSlots(new: List<String>) {
        slots = new
        saveSlots(prefs, new)
    }

    fun togglePin(app: AppEntry) {
        if (app.pkg in slots) {
            updateSlots(slots.map { if (it == app.pkg) "" else it })
        } else {
            val empty = slots.indexOfFirst { it.isEmpty() }
            val target = if (empty >= 0) empty else SLOT_COUNT - 1
            updateSlots(slots.toMutableList().also { it[target] = app.pkg })
        }
    }

    // 스와이프/단계 이동 시 선택한 앱이 있는 그룹으로 로스터 스크롤
    LaunchedEffect(scrollTick) {
        if (scrollTick == 0) return@LaunchedEffect
        val pkg = selected?.pkg ?: return@LaunchedEffect
        val gi = groups.indexOfFirst { g -> g.second.any { it.pkg == pkg } }
        if (gi >= 0) {
            val col = groups[gi].second.indexOfFirst { it.pkg == pkg } / 2
            val off = with(density) { (col * 57).dp.roundToPx() }
            rosterState.animateScrollToItem(gi, off)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgMid, BgBottom)))
    ) {
        BackgroundDecor()

        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Header(
                selected = selected,
                total = apps.size,
                index = selectedIndex,
                time = clock.first,
                date = clock.second
            )

            Hero(
                selected = selected,
                onStep = { d -> step(d) },
                onLaunch = { selected?.let { launchApp(ctx, it.pkg) } },
                modifier = Modifier.weight(1f)
            )

            TeamRow(
                slots = slots,
                apps = apps,
                selectedPkg = selected?.pkg,
                onSelect = { select(it) },
                onUnpin = { index -> updateSlots(slots.toMutableList().also { it[index] = "" }) }
            )

            Spacer(Modifier.height(12.dp))

            Roster(
                groups = groups,
                selectedPkg = selected?.pkg,
                slots = slots,
                state = rosterState,
                onSelect = { select(it) },
                onTogglePin = { togglePin(it) }
            )

            Spacer(Modifier.height(12.dp))

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ConfirmButton(enabled = selected != null) {
                    selected?.let { launchApp(ctx, it.pkg) }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KeyHint("F1", "앱 정보") { selected?.let { openAppInfo(ctx, it.pkg) } }
                    Spacer(Modifier.width(14.dp))
                    KeyHint("DEL", "삭제") { selected?.let { uninstallApp(ctx, it.pkg) } }
                }
                KeyHint("HOLD", "슬롯 고정", null)
            }
        }
    }
}

// ───────────── 배경 장식 ─────────────
@Composable
private fun BackgroundDecor() {
    Canvas(Modifier.fillMaxSize()) {
        val w = this.size.width
        val h = this.size.height

        val beam1 = Path().apply {
            moveTo(w * 0.50f, 0f)
            lineTo(w * 0.80f, 0f)
            lineTo(w * 0.30f, h)
            lineTo(w * 0.00f, h)
            close()
        }
        drawPath(beam1, Brush.verticalGradient(listOf(Color(0x16FFFFFF), Color.Transparent)))

        val beam2 = Path().apply {
            moveTo(w * 0.85f, 0f)
            lineTo(w * 1.00f, 0f)
            lineTo(w * 0.70f, h)
            lineTo(w * 0.55f, h)
            close()
        }
        drawPath(beam2, Brush.verticalGradient(listOf(Color(0x0DFFFFFF), Color.Transparent)))

        // 하단 비네팅
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color.Transparent, Color(0x99000000)),
                startY = h * 0.55f,
                endY = h
            )
        )
    }
}

// ───────────── 글자 효과 ─────────────
@Composable
private fun GhostText(
    text: String,
    size: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    align: TextAlign = TextAlign.Start
) {
    Box(modifier) {
        // 뒤쪽 잔상
        Text(
            text = text,
            color = color.copy(alpha = 0.22f),
            fontSize = size,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = align,
            modifier = Modifier.offset(4.dp, 3.dp)
        )
        Text(
            text = text,
            color = color,
            fontSize = size,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = align,
            style = TextStyle(
                shadow = Shadow(color = Color(0x99000000), offset = Offset(3f, 3f), blurRadius = 6f)
            )
        )
    }
}

// ───────────── 그룹 아이콘 (탱커/딜러/힐러 아이콘 역할) ─────────────
@Composable
private fun GroupIcon(group: AppGroup, iconSize: Dp, color: Color) {
    Canvas(Modifier.size(iconSize)) {
        val w = this.size.width
        val h = this.size.height
        when (group) {
            AppGroup.GAME -> {
                val bw = w * 0.2f
                listOf(0.08f, 0.40f, 0.72f).forEach { x ->
                    drawRect(color, Offset(w * x, h * 0.1f), Size(bw, h * 0.8f))
                }
            }
            AppGroup.SOCIAL -> {
                drawRect(color, Offset(w * 0.38f, 0f), Size(w * 0.24f, h))
                drawRect(color, Offset(0f, h * 0.38f), Size(w, h * 0.24f))
            }
            AppGroup.MEDIA -> {
                val p = Path().apply {
                    moveTo(w * 0.2f, h * 0.08f)
                    lineTo(w * 0.92f, h * 0.5f)
                    lineTo(w * 0.2f, h * 0.92f)
                    close()
                }
                drawPath(p, color)
            }
            AppGroup.TOOL -> {
                val p = Path().apply {
                    moveTo(w * 0.1f, h * 0.1f)
                    lineTo(w * 0.9f, h * 0.1f)
                    lineTo(w * 0.9f, h * 0.55f)
                    lineTo(w * 0.5f, h * 0.95f)
                    lineTo(w * 0.1f, h * 0.55f)
                    close()
                }
                drawPath(p, color)
            }
            AppGroup.ETC -> {
                drawCircle(color, radius = w * 0.34f, style = Stroke(w * 0.16f))
            }
            AppGroup.SYSTEM -> {
                drawRect(
                    color,
                    Offset(w * 0.12f, h * 0.12f),
                    Size(w * 0.76f, h * 0.76f),
                    style = Stroke(w * 0.16f)
                )
            }
        }
    }
}

@Composable
private fun Badge(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Color(0xDD0E1522))
            .border(1.dp, SlotLine, CircleShape),
        contentAlignment = Alignment.Center
    ) { content() }
}

// ───────────── 헤더 ─────────────
@Composable
private fun Header(selected: AppEntry?, total: Int, index: Int, time: String, date: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xCC070B14), Color.Transparent)))
            .drawBehind {
                drawLine(
                    Color(0x55FFFFFF),
                    Offset(0f, this.size.height),
                    Offset(this.size.width, this.size.height),
                    1.dp.toPx()
                )
            }
            .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            // 좌측: 타이틀 + 시계
            Column(Modifier.weight(1f)) {
                GhostText("런처", 36.sp)
                Text(
                    "앱을 선택하십시오",
                    color = IceBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
                Spacer(Modifier.height(10.dp))
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Box(
                        Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .background(Orange)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "현재 시각",
                            color = Orange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            time,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(date, color = SlotLine, fontSize = 11.sp)
                    }
                }
            }

            // 우측: 선택한 앱 이름 + 분류
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                val name = selected?.label ?: "-"
                val nameSize = when {
                    name.length <= 8 -> 32.sp
                    name.length <= 14 -> 24.sp
                    else -> 18.sp
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "+",
                        color = Orange,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.width(4.dp))
                    GhostText(name, nameSize, align = TextAlign.End)
                }
                if (selected != null) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            selected.group.label,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .size(24.dp, 26.dp)
                                .clip(HexShape)
                                .border(1.5.dp, Orange, HexShape),
                            contentAlignment = Alignment.Center
                        ) {
                            GroupIcon(selected.group, 11.dp, Orange)
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${index + 1} / $total",
                        color = SlotLine,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ───────────── 중앙 영웅(앱) 영역 ─────────────
@Composable
private fun Hero(
    selected: AppEntry?,
    onStep: (Int) -> Unit,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stepState by rememberUpdatedState(onStep)

    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                var total = 0f
                detectHorizontalDragGestures(
                    onDragStart = { total = 0f },
                    onDragEnd = {
                        if (total < -70f) stepState(1) else if (total > 70f) stepState(-1)
                    },
                    onDragCancel = { total = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        total += dragAmount
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val iconSize = minOf(170.dp, maxHeight * 0.62f)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                // 발광
                Box(
                    Modifier
                        .size(iconSize * 1.5f)
                        .background(
                            Brush.radialGradient(listOf(Color(0x66F99E1A), Color.Transparent))
                        )
                )
                // 육각 링
                Box(
                    Modifier
                        .size(width = iconSize * 1.12f, height = iconSize * 1.28f)
                        .clip(HexShape)
                        .background(Color(0x14FFFFFF))
                        .border(1.5.dp, Color(0x66B9C6DA), HexShape)
                )
                Crossfade(targetState = selected, label = "hero") { app ->
                    if (app != null) {
                        Image(
                            bitmap = app.icon,
                            contentDescription = app.label,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(iconSize)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onLaunch() }
                        )
                    }
                }
            }
            // 바닥 그림자
            Box(
                Modifier
                    .width(iconSize * 0.9f)
                    .height(12.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0x99000000), Color.Transparent))
                    )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                selected?.pkg ?: "",
                color = SlotLine.copy(alpha = 0.55f),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 이전 / 다음 힌트
        Text(
            "‹ 이전",
            color = SlotLine.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { stepState(-1) }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        )
        Text(
            "다음 ›",
            color = SlotLine.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { stepState(1) }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        )
    }
}

// ───────────── 육각 슬롯 줄 (즐겨찾기) ─────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TeamRow(
    slots: List<String>,
    apps: List<AppEntry>,
    selectedPkg: String?,
    onSelect: (String) -> Unit,
    onUnpin: (Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .drawBehind {
                val w = this.size.width
                val y = this.size.height - 1.dp.toPx()
                val t = 12.dp.toPx()
                val p = Path().apply {
                    moveTo(0f, y - t)
                    lineTo(t, y)
                    lineTo(w - t, y)
                    lineTo(w, y - t)
                }
                drawPath(p, color = Color(0x77B9C6DA), style = Stroke(1.dp.toPx()))
            }
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        slots.forEachIndexed { index, pkg ->
            val app = apps.firstOrNull { it.pkg == pkg }
            val isSel = app != null && app.pkg == selectedPkg
            val line = when {
                isSel -> Orange
                app != null -> SlotBlue
                else -> SlotLine.copy(alpha = 0.6f)
            }

            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(width = 62.dp, height = 68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(width = 54.dp, height = 62.dp)
                            .drawBehind {
                                if (isSel) {
                                    drawCircle(
                                        Brush.radialGradient(
                                            listOf(Color(0x99F99E1A), Color.Transparent)
                                        ),
                                        radius = this.size.maxDimension * 0.8f
                                    )
                                }
                            }
                            .clip(HexShape)
                            .background(if (app != null) Color(0xFF22304A) else Color(0x66101826))
                            .border(if (isSel) 3.dp else 2.dp, line, HexShape)
                            .combinedClickable(
                                onClick = { if (app != null) onSelect(app.pkg) },
                                onLongClick = { if (app != null) onUnpin(index) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (app != null) {
                            Image(
                                bitmap = app.icon,
                                contentDescription = app.label,
                                modifier = Modifier.size(34.dp)
                            )
                        } else {
                            Text(
                                "•••",
                                color = SlotLine,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (app != null) {
                        Badge(Modifier.align(Alignment.TopStart)) {
                            GroupIcon(app.group, 8.dp, Color.White)
                        }
                    }
                    Badge(Modifier.align(Alignment.TopEnd)) {
                        Text(
                            "${index + 1}",
                            color = SlotLine,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    app?.label ?: "—",
                    color = if (app != null) NameBlue else SlotLine.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(62.dp)
                )
            }
        }
    }
}

// ───────────── 하단 로스터 (그룹별) ─────────────
@Composable
private fun Roster(
    groups: List<Pair<AppGroup, List<AppEntry>>>,
    selectedPkg: String?,
    slots: List<String>,
    state: androidx.compose.foundation.lazy.LazyListState,
    onSelect: (String) -> Unit,
    onTogglePin: (AppEntry) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(PanelDark)
            .padding(vertical = 8.dp)
    ) {
        LazyRow(
            state = state,
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(groups, key = { it.first.ordinal }) { gp ->
                val group = gp.first
                val list = gp.second
                Column(Modifier.width(IntrinsicSize.Max)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GroupIcon(group, 12.dp, Color.White)
                        Spacer(Modifier.width(5.dp))
                        Text(
                            group.label,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("${list.size}", color = SlotLine, fontSize = 10.sp)
                    }
                    Spacer(Modifier.height(5.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        list.chunked(2).forEach { pair ->
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                pair.forEach { app ->
                                    AppTile(
                                        app = app,
                                        isSel = app.pkg == selectedPkg,
                                        isPinned = app.pkg in slots,
                                        onClick = { onSelect(app.pkg) },
                                        onLongClick = {
                                            onSelect(app.pkg)
                                            onTogglePin(app)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(5.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x66B9C6DA))
                    )
                    Text(
                        group.sub,
                        color = SlotLine,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppTile(
    app: AppEntry,
    isSel: Boolean,
    isPinned: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSel) 1.08f else 1f,
        animationSpec = tween(120),
        label = "tile"
    )
    Box(
        Modifier
            .size(52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                if (isSel) {
                    val pad = 8.dp.toPx()
                    drawRect(
                        brush = Brush.radialGradient(
                            listOf(Color(0x66F99E1A), Color.Transparent)
                        ),
                        topLeft = Offset(-pad, -pad),
                        size = Size(this.size.width + pad * 2, this.size.height + pad * 2)
                    )
                }
            }
            .background(Color(0xFF1B263B))
            .border(
                if (isSel) 3.dp else 1.dp,
                if (isSel) Orange else Color(0x55FFFFFF)
            )
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = app.icon,
            contentDescription = app.label,
            modifier = Modifier
                .size(42.dp)
                .alpha(if (isSel) 1f else 0.85f)
        )
        if (isPinned) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(6.dp)
                    .background(Orange, CircleShape)
            )
        }
        if (isSel) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color(0xDD000000)))
                    )
                    .padding(top = 6.dp, bottom = 1.dp)
            ) {
                Text(
                    app.label,
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ───────────── 확인 버튼 / 키 힌트 ─────────────
@Composable
private fun ConfirmButton(enabled: Boolean, onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(80),
        label = "btn"
    )
    Box(
        Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .width(220.dp)
            .height(48.dp)
            .background(
                Brush.verticalGradient(
                    if (pressed) listOf(OrangeDark, Color(0xFFB86A00))
                    else listOf(Color(0xFFFFB347), Orange, OrangeDark)
                )
            )
            .border(1.dp, Color(0x99FFD08A))
            .clickable(interactionSource = src, indication = null, enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "확인",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )
    }
}

@Composable
private fun KeyHint(key: String, label: String, onClick: (() -> Unit)?) {
    val base = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Row(base.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .background(Color(0xFF0E1522))
                .border(1.dp, Color(0x88B9C6DA))
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(key, color = SlotLine, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(5.dp))
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
