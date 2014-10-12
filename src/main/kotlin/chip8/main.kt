package chip8;

import java.io.FileInputStream
import java.io.BufferedInputStream
import java.io.DataInputStream

fun main(args: Array<String>) {
    val vmState = loadRom("roms/maze.rom")
    println(disassemble(vmState))
}