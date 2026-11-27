package com.example.habittracker   

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.ui.theme.HabitTrackerTheme

/*
 * ЗАДАНИЕ 1. Создание проекта
 * MainActivity подключает Compose через setContent { }. Внутри темы вызываем
 * HabitScreen вместо Greeting. HabitScreen принимает modifier: Modifier = Modifier
 * и не игнорирует его.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme {
                HabitScreen()
            }
        }
    }
}

/*
 * ЗАДАНИЕ 6. Модель данных (вне Composable)
 * У каждой привычки уникальный id. Все поля val: запись в списке заменяется
 * через copy(), а не меняется "на месте".
 */
data class Habit(
    val id: Int,
    val name: String,
    val minutes: Int,
    val category: String,
    val required: Boolean = false,
    val daily: Boolean = false,
    val difficulty: Int = 1,
    val done: Boolean = false
)

// ЗАДАНИЕ 5: варианты категорий
private val categories = listOf("Здоровье", "Образование", "Работа", "Отдых")

@Composable
fun HabitScreen(modifier: Modifier = Modifier) {

    /*
     * ЗАДАНИЕ 6. Единый наблюдаемый список
     * mutableStateListOf отслеживает изменения самого списка (add/remove/set).
     * Пять исходных привычек: id от 1 до 5.
     * Для новых записей — отдельный счётчик id, начиная с 6. Размер списка как
     * id использовать нельзя: после удаления id повторились бы.
     */
    val habits = remember {
        mutableStateListOf(
            Habit(1, "Чтение", 30, "Образование"),
            Habit(2, "Программирование", 60, "Работа"),
            Habit(3, "Английский", 20, "Образование"),
            Habit(4, "Тренировка", 40, "Здоровье"),
            Habit(5, "Медитация", 10, "Отдых")
        )
    }
    var nextId by remember { mutableIntStateOf(6) }

    /*
     * ЗАДАНИЕ 4. Состояние и рекомпозиция
     * remember сохраняет состояние между рекомпозициями, mutableStateOf делает
     * его наблюдаемым, by позволяет писать count вместо count.value.
     * Обычный var count = 0 НЕ наблюдаемый: интерфейс не обновится.
     */
    var createdCount by remember { mutableIntStateOf(0) }
    var lastAdded by remember { mutableStateOf<Habit?>(null) } // последняя введённая (задание 5)

    /*
     * ЗАДАНИЕ 5. Состояния формы
     * Категория — ОДНО состояние String (а не отдельные Boolean на каждую).
     * Время хранится строкой, потому что TextField работает со строкой.
     * Slider работает с Float, поэтому difficulty — Float.
     */
    var name by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.first()) }
    var required by remember { mutableStateOf(false) }
    var daily by remember { mutableStateOf(false) }
    var difficulty by remember { mutableFloatStateOf(1f) }

    /*
     * ЗАДАНИЕ 5. Валидация
     * toIntOrNull() не падает на пустой строке и буквах (в отличие от toInt()).
     * Кнопка «Добавить» активна при непустом названии после trim() и
     * положительном целом времени. KeyboardType.Number — лишь подсказка
     * клавиатуре, а не гарантия корректного ввода, поэтому проверяем сами.
     */
    val minutesValue = minutes.toIntOrNull()?.takeIf { it > 0 }
    val canAdd = name.trim().isNotEmpty() && minutesValue != null
    val minutesError = minutes.isNotEmpty() && minutesValue == null

    /*
     * ЗАДАНИЕ 7. Статистика
     * Считается из списка как вычисляемые val.
     * Отдельные изменяемые счётчики не заводим.
     */
    val completed = habits.count { it.done }
    val notCompleted = habits.size - completed
    val doneMinutes = habits.filter { it.done }.sumOf { it.minutes }
    val percent = if (habits.isEmpty()) 0 else completed * 100 / habits.size

    // Обработчики событий
    // ЗАДАНИЕ 4–5: состояние меняем ТОЛЬКО в обработчиках, не в теле Composable.

    // «Очистить»: возвращает поля к исходным значениям, список НЕ удаляет
    fun clearForm() {
        name = ""
        minutes = ""
        category = categories.first()
        required = false
        daily = false
        difficulty = 1f
    }

    // ЗАДАНИЕ 6: «Добавить» — создаём Habit из формы, добавляем в список, чистим поля
    fun addHabit() {
        val m = minutesValue ?: return
        if (name.trim().isEmpty()) return
        val habit = Habit(
            id = nextId,
            name = name.trim(),
            minutes = m,
            category = category,
            required = required,
            daily = daily,
            difficulty = difficulty.toInt()
        )
        nextId++
        habits.add(habit)
        lastAdded = habit
        createdCount++
        clearForm()
    }

    // ЗАДАНИЕ 7: переключение выполнения. Ищем по id, а не по позиции.
    fun toggle(id: Int) {
        val index = habits.indexOfFirst { it.id == id }
        if (index >= 0) {
            habits[index] = habits[index].copy(done = !habits[index].done)
        }
    }

    // ЗАДАНИЕ 6: удаление по id
    fun delete(id: Int) {
        habits.removeAll { it.id == id }
    }

    // ЗАДАНИЕ 7: «Сбросить статистику» снимает все отметки, привычки остаются
    fun resetStats() {
        for (i in habits.indices) {
            if (habits[i].done) habits[i] = habits[i].copy(done = false)
        }
    }

    /*
     * ЗАДАНИЯ 3 и 5. Один LazyColumn на весь экран
     * Блоки экрана — это item { }, привычки — items(...) { }.
     * Нельзя класть LazyColumn в Column с verticalScroll (бесконечная высота).
     * statusBarsPadding() — отступ от строки состояния сверху.
     * imePadding() — чтобы клавиатура не закрывала поля формы.
     * Arrangement.spacedBy(12.dp) — промежуток МЕЖДУ детьми, не от краёв.
     * Переданный modifier применяется к LazyColumn.
     */
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        /*
         * ЗАДАНИЯ 2 и 8. Заголовок + изображение
         * Row: элементы по горизонтали; verticalAlignment по центру.
         * Логотип: Box с CircleShape обрезкой 64.dp. В задании используется
         * Image из drawable (файл в res/drawable, имя латиницей):
         *
         *   Image(
         *       painter = painterResource(R.drawable.habit_logo),
         *       contentDescription = "Логотип трекера",
         *       contentScale = ContentScale.Crop,
         *       modifier = Modifier.size(64.dp).clip(CircleShape)
         *   )
         *
         * Чтобы код не зависел от файла картинки, ниже нарисован эквивалент.
         */
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Логотип трекера",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text(
                    text = "Трекер привычек",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        /*
         * ЗАДАНИЯ 2, 3, 4, 7. Блок статистики через Surface
         * Surface: color, shape, tonalElevation, shadowElevation, border.
         * Внутри Column с отступами 16.dp и промежутками 8.dp.
         */
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ЗАДАНИЕ 2 + 7: «Выполнено сегодня» / «N из M» (теперь не статика)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Выполнено сегодня", fontWeight = FontWeight.SemiBold)
                        Text("$completed из ${habits.size}")
                    }
                    // ЗАДАНИЕ 7: остальные показатели
                    Text("Не выполнено: $notCompleted")
                    Text("Минут выполнено: $doneMinutes")
                    // ЗАДАНИЕ 4: счётчик созданных привычек
                    Text("Создано привычек: $createdCount")

                    // ЗАДАНИЕ 2: Box высотой 100.dp.
                    // Box накладывает элементы друг на друга:
                    // процент в центре, «Прогресс» — сверху слева (Modifier.align).
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "Прогресс",
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$percent%",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    /*
                     * ЗАДАНИЕ 4. Кнопки трёх видов (сравни внешний вид):
                     * Button — залитая, OutlinedButton — с контуром,
                     * TextButton — без заливки.
                     * onClick — лямбда, вызывается при нажатии
                     * (count++ напрямую в теле Composable писать нельзя).
                     * enabled = false — обработчик не выполняется.
                     * После задания 5 назначение кнопок изменилось:
                     * «Добавить» создаёт привычку, «Очистить» чистит поля.
                     */
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { addHabit() },
                            enabled = canAdd,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(12.dp)
                        ) { Text("Добавить") }

                        OutlinedButton(onClick = { clearForm() }) { Text("Очистить") }
                    }
                    // ЗАДАНИЕ 7: снимает отметки, привычки не удаляются
                    TextButton(onClick = { resetStats() }) { Text("Сбросить статистику") }

                    // ЗАДАНИЕ 5: вывод последней введённой привычки
                    lastAdded?.let {
                        Text(
                            "Последняя: ${it.name}, ${it.minutes} мин, ${it.category}",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // ЗАДАНИЕ 5. Форма ввода (всегда видна)
        item {
            HabitForm(
                name = name, onNameChange = { name = it },
                minutes = minutes, onMinutesChange = { minutes = it },
                minutesError = minutesError,
                category = category, onCategoryChange = { category = it },
                required = required, onRequiredChange = { required = it },
                daily = daily, onDailyChange = { daily = it },
                difficulty = difficulty, onDifficultyChange = { difficulty = it }
            )
        }

        /*
         * ЗАДАНИЯ 3, 6, 7. Список привычек
         * items(habits, key = { it.id }) — key по id, чтобы Compose
         * правильно отслеживал карточки при удалении.
         * Карточка только сообщает о событии (onToggle/onDelete),
         * а сам список меняется здесь, в HabitScreen.
         * Если список пуст — понятное сообщение.
         */
        if (habits.isEmpty()) {
            item {
                Text(
                    text = "Привычек пока нет. Добавьте первую в форме выше.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                HabitCard(
                    habit = habit,
                    onToggle = { toggle(habit.id) },
                    onDelete = { delete(habit.id) }
                )
            }
        }
    }
}

/*
 * ЗАДАНИЯ 3, 7, 8. Карточка привычки
 * ЗАДАНИЕ 3: Card с Row: название слева, время справа.
 *   Card: shape — форма, colors — CardDefaults.cardColors, elevation — тень.
 * ЗАДАНИЕ 7: получает ТЕКУЩЕЕ состояние и лямбды onToggle / onDelete.
 *   Checkbox + нажатие на карточку меняют выполнение.
 *   Выполненная — другой цвет и TextDecoration.LineThrough у названия.
 * ЗАДАНИЕ 8: IconButton удаляет, галочка показывается у выполненной.
 *   Row.weight(1f) у Column — название занимает всё свободное место,
 *   время и кнопки прижимаются вправо.
 */
@Composable
fun HabitCard(
    habit: Habit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.done) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = habit.done, onCheckedChange = { onToggle() })

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (habit.done) TextDecoration.LineThrough else null
                )
                val extra = buildList {
                    add(habit.category)
                    if (habit.required) add("обязательная")
                    if (habit.daily) add("ежедневно")
                    add("сложность ${habit.difficulty}")
                }.joinToString(" • ")
                Text(extra, fontSize = 12.sp)
            }

            Text("${habit.minutes} мин", fontWeight = FontWeight.SemiBold)

            if (habit.done) {
                Icon(
                    Icons.Default.Check, // свой вариант: painterResource(R.drawable.ic_check)
                    contentDescription = "Выполнено",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete, // свой вариант: painterResource(R.drawable.ic_delete)
                    contentDescription = "Удалить привычку"
                )
            }
        }
    }
}

/*
 * ЗАДАНИЕ 5. Форма ввода (отдельная функция: состояние и обработчики передаются
 * параметрами — это называется "подъём состояния", само состояние живёт в
 * HabitScreen).
 * OutlinedTextField: value — текущее значение, onValueChange — новое значение
 *   приходит в it, label — название, singleLine — один ряд,
 *   isError + supportingText — признак и текст ошибки,
 *   keyboardOptions — тип клавиатуры (цифровая).
 * Checkbox: checked / onCheckedChange. Switch — те же параметры, но для
 *   переключения настройки.
 * RadioButton: selected — выбран ли конкретный вариант; onClick — выбор.
 *   Строка с RadioButton кликабельна целиком.
 * Slider: value (Float), valueRange 1f..5f, steps = 3 (промежуточные точки без
 *   крайних → доступны 1, 2, 3, 4, 5). Показываем difficulty.toInt().
 */
@Composable
fun HabitForm(
    name: String, onNameChange: (String) -> Unit,
    minutes: String, onMinutesChange: (String) -> Unit,
    minutesError: Boolean,
    category: String, onCategoryChange: (String) -> Unit,
    required: Boolean, onRequiredChange: (Boolean) -> Unit,
    daily: Boolean, onDailyChange: (Boolean) -> Unit,
    difficulty: Float, onDifficultyChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Новая привычка", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Название") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = minutes,
                onValueChange = onMinutesChange,
                label = { Text("Время, мин") },
                singleLine = true,
                isError = minutesError,
                supportingText = {
                    if (minutesError) Text("Введите целое число больше 0")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = required, onCheckedChange = onRequiredChange)
                Text("Обязательная")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = daily, onCheckedChange = onDailyChange)
                Spacer(Modifier.width(8.dp))
                Text("Ежедневно")
            }

            Text("Категория", fontWeight = FontWeight.SemiBold)
            categories.forEach { c ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryChange(c) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = category == c, onClick = { onCategoryChange(c) })
                    Text(c)
                }
            }

            Text("Сложность: ${difficulty.toInt()}", fontWeight = FontWeight.SemiBold)
            Slider(
                value = difficulty,
                onValueChange = onDifficultyChange,
                valueRange = 1f..5f,
                steps = 3
            )
        }
    }
}

/*
 * МИНИ-ЭКСПЕРИМЕНТ: порядок Modifier (в приложении не вызывается;
 * чтобы посмотреть, временно вызови ModifierExperiment() в setContent)
 *  A) background().padding(): фон идёт СНАРУЖИ отступа, поэтому покрывает и
 *     контент, и отступ → зелёный блок большой.
 *  B) padding().background(): отступ снаружи, фон только под контентом →
 *     зелёный блок маленький, вокруг него пустое место.
 *  C) border().padding() даёт пространство ВНУТРИ границы,
 *     а padding().border() — снаружи.
 */
@Composable
fun ModifierExperiment() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("A: background → padding", Modifier.background(Color.Green).padding(16.dp))
        Text("B: padding → background", Modifier.padding(16.dp).background(Color.Green))
        Text(
            "C: padding, border, background, padding",
            Modifier
                .padding(8.dp)
                .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
                .background(Color.LightGray, RoundedCornerShape(12.dp))
                .padding(16.dp)
        )
    }
}
