package chip8;

import java.io.FileInputStream
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.HashSet

fun main(args: Array<String>) {
    val vmState = chip8.loadRom("roms/maze.rom")
    Debugger().run(vmState)
}