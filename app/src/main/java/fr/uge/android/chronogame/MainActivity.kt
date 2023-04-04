package fr.uge.android.chronogame

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.android.chronogame.ui.theme.ChronogameTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChronogameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //DeltaTimeDisplayer(deltaTime = 400000)
                    //Chronometer(startTime = SystemClock.elapsedRealtime(), SystemClock.elapsedRealtime()+10*1000)
                    //ChronometerManager()
                    /*ChronoGame(expectedDuration = 10*1000, onVerdict = { time: Long->
                        val minutes: Long = (time/1000)/60
                        val seconds: Long = (time/1000)%60
                        val milliseconds: Long = time%1000
                    })*/
                    GameManager()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun DefaultPreview() {
    ChronogameTheme {
        Greeting("Android")
    }
}

@Composable
fun DeltaTimeDisplayer(deltaTime: Long = 400200){
    val minutes: Long = (deltaTime/1000)/60
    val seconds: Long = (deltaTime/1000)%60
    val milliseconds: Long = deltaTime%1000
    Text(text = "$minutes "+": $seconds"+": $milliseconds")
}

@Composable
fun Chronometer(startTime: Long, endTime: Long = -1){
    var currentTime = remember{
        mutableStateOf(SystemClock.elapsedRealtime())
    }
    if (!endTime.equals(-1L)){
        DeltaTimeDisplayer( endTime - startTime)
    }
    else{
        DeltaTimeDisplayer( currentTime.value - startTime)
    }
    LaunchedEffect(Unit){
        while (true){
            delay(1)
            currentTime.value = SystemClock.elapsedRealtime()
        }
    }
}

@Composable fun ChronometerManager(){
    var startTime  by remember {mutableStateOf(SystemClock.elapsedRealtime())}
    var endTime by remember {mutableStateOf(SystemClock.elapsedRealtime())}
    Column() {
        Row() {
            Button(onClick = {
                startTime = SystemClock.elapsedRealtime()
                endTime = -1L}
            ) {
                Text(text = "Start")
            }
            Button(onClick = {

                endTime = SystemClock.elapsedRealtime()}) {
                Text(text = "End")
            }
        }
        Chronometer(startTime = startTime, endTime = endTime)
    }
}
@Composable
fun ChronoGame(expectedDuration: Long, onVerdict: (Long) -> Unit){
    var startTime  by remember {mutableStateOf(SystemClock.elapsedRealtime())}
    var time2  by remember {mutableStateOf(SystemClock.elapsedRealtime())}
    var trigger  by remember {mutableStateOf(-1L)}
    var endTime by remember {mutableStateOf(SystemClock.elapsedRealtime())}
    Column() {
        Row() {
            Button(onClick = {
                startTime = SystemClock.elapsedRealtime()
                trigger = 1L
                endTime = -1L}
            ) {
                Text(text = "Start")
            }
            Button(onClick = {
                endTime = SystemClock.elapsedRealtime()
                trigger = -1L
                onVerdict(endTime-startTime)}) {
                Text(text = "End")
            }
        }
        LaunchedEffect(Unit){
            while (true){
                delay(100)
                time2 = SystemClock.elapsedRealtime()
            }
        }
        Box(){
            Chronometer(startTime = startTime, endTime = endTime)
            if (trigger != -1L){
                if ((time2 - startTime)>= expectedDuration/2){
                    Text(modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth(), text = "")
                }
            }

        }
    }
}

@Composable
fun GameManager(){
    var gameState by remember{
        mutableStateOf(State.State1)
    }
    var expectedDuration by remember{
        mutableStateOf(0L)
    }
    var stopedTime by remember{
        mutableStateOf(0L)
    }
    if(gameState == State.State1){
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn (modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth()){
                items(100) { index ->
                    Text(text = ""+(index+1)+" s", Modifier.clickable {
                        expectedDuration = ((index+1)*1000).toLong()
                    })
                }
            }
            Button(onClick = { gameState = State.State2 }, modifier = Modifier.fillMaxHeight(0.3f)) {
                Text(text = "Start game")
            }
        }
    }
    else if( gameState == State.State2){
        ChronoGame(expectedDuration = expectedDuration){
            stopedTime = it
            gameState = State.State3
        }
    }
    else if (gameState == State.State3){
        val s = when {
            expectedDuration > stopedTime -> "-"
            else -> "+"
        }
        Column {
            DeltaTimeDisplayer(stopedTime)
            Text(text = "Your score : $s" +(100-(stopedTime*100L)/expectedDuration)+"%")
            Button(onClick = { gameState = State.State1 }) {
                Text(text = "Replay")
            }
        }
    }
    /*
    * expeted = 100
    * stopedtime = x
    * */
}
enum class State{
    State1,
    State2,
    State3,
}