package com.purrlogic.game

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val cream = Color(0xFFF8F2ED)
private val ink = Color(0xFF926D70)
private val shades = listOf(Color(0xFFE9BF55),Color(0xFFF4B3CE),Color(0xFF76BEE1),Color(0xFFB2D99F))
private val answers = listOf(listOf(1,3,0,2),listOf(2,0,3,1),listOf(1,3,0,2),listOf(2,0,3,1))
class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
   val prefs=getSharedPreferences("progress", MODE_PRIVATE)
   setContent {
     var level by remember { mutableIntStateOf(prefs.getInt("level",1)) }
     var marks by remember(level) { mutableStateOf(List(16){0}) }
     var message by remember { mutableStateOf("각 행, 열, 색깔마다 고양이 한 마리!") }
     var showSettings by remember { mutableStateOf(false) }
     val solution=answers[(level-1)%answers.size]
     val cats=marks.indices.filter { marks[it]==2 }
     val correct=cats.size==4 && cats.all { i->solution[i/4]==i%4 }
     LaunchedEffect(correct) { if(correct) message="완벽해요! 다음 레벨로 가볼까요? 🎉" }
     MaterialTheme {
       Column(Modifier.fillMaxSize().background(cream).padding(18.dp),horizontalAlignment=Alignment.CenterHorizontally) {
         Spacer(Modifier.height(22.dp))
         Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
           Text("냥냥 로직",fontSize=23.sp,color=ink,fontWeight=FontWeight.Bold)
           TextButton(onClick={showSettings=true}) { Text("⚙ 설정",color=ink) }
         }
         Spacer(Modifier.height(25.dp))
         Text("LEVEL $level",fontSize=28.sp,fontWeight=FontWeight.Bold,color=ink)
         Spacer(Modifier.height(12.dp))
         Text(message,color=ink,textAlign=TextAlign.Center,fontSize=15.sp)
         Spacer(Modifier.height(26.dp))
         Column(Modifier.fillMaxWidth().background(Color.White,RoundedCornerShape(20.dp)).padding(9.dp)) {
           repeat(4){ r->
             Row(Modifier.fillMaxWidth()) {
               repeat(4){ c->
                 val index=r*4+c
                 Box(Modifier.weight(1f).aspectRatio(1f).padding(3.dp).background(shades[c],RoundedCornerShape(8.dp)).clickable {
                   if(!correct) {
                     marks=marks.toMutableList().also { it[index]=(it[index]+1)%3 }
                     message="한 번 탭 X · 두 번 탭 고양이"
                   }
                 },contentAlignment=Alignment.Center) {
                   Text(when(marks[index]) {1->"✕";2->"🐱";else->""},fontSize=36.sp,color=Color.White,fontWeight=FontWeight.Bold)
                 }
               }
             }
           }
         }
         Spacer(Modifier.height(20.dp))
         Text("색깔별 · 행별 · 열별 한 마리, 대각선 인접 금지",fontSize=13.sp,color=ink,textAlign=TextAlign.Center)
         Spacer(Modifier.height(20.dp))
         Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
           OutlinedButton(onClick={marks=List(16){0};message="처음부터 다시 도전!"}) { Text("다시 시작") }
           OutlinedButton(onClick={
             val hint=solution.indices.firstOrNull { marks[it*4+solution[it]]!=2 }
             if(hint!=null){marks=marks.toMutableList().also{it[hint*4+solution[hint]]=2};message="힌트: 고양이 한 마리를 놓았어요"}
           }) { Text("힌트") }
         }
         Spacer(Modifier.height(14.dp))
         if(correct) Button(onClick={level++;prefs.edit().putInt("level",level).apply();message="새 퍼즐 시작!"},colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFF59A23))) { Text("다음 레벨 →") }
         Spacer(Modifier.weight(1f))
         Text("PurrLogic · prototype 0.1",color=ink,fontSize=12.sp)
       }
       if(showSettings) AlertDialog(onDismissRequest={showSettings=false},title={Text("설정")},text={Text("한 번 탭하면 X, 두 번 탭하면 고양이, 세 번 탭하면 빈 칸으로 돌아갑니다.\n\n광고는 아직 연결되지 않았습니다.")},confirmButton={TextButton(onClick={showSettings=false}){Text("닫기")}})
     }
   }
 }
}
