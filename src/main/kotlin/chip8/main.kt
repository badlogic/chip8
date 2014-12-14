package chip8;

import java.io.FileInputStream
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.HashSet

fun main(args: Array<String>) {
    val vmState = chip8.loadRom("roms/maze.rom")
    val interpreter = Interpreter(vmState)
    val reader = BufferedReader(InputStreamReader(System.`in`))
    val breakpoints = HashSet<Int>()
    while(true) {
        print("> ")
        val line = reader.readLine()
        if(line != null) {
            val tokens = line.split("\\s+")
            if(tokens.size > 0) {
                when(tokens[0]) {
                    "n" -> {
                        interpreter.step()
                        if(breakpoints.contains(vmState.pc)) {
                            println("Hit breakpoint at 0x${vmState.pc.hex}")
                            break
                        }
                        print(chip8.disassemble(vmState, vmState.pc, 2))
                    }
                    "r" -> {
                        var pc = vmState.pc
                        do {
                            pc = vmState.pc
                            interpreter.step()
                            if(breakpoints.contains(vmState.pc)) {
                                println("Hit breakpoint at 0x${vmState.pc.hex}")
                                break
                            }
                        } while(pc != vmState.pc)
                        print(chip8.disassemble(vmState, vmState.pc, 2))
                    }
                    "b" -> {
                        breakpoints.add(Integer.parseInt(tokens[1].replace("0x", ""), 16))
                    }
                    "s" -> print(vmState)
                    "v" -> print(vmState.vramToString())
                    "d" -> print(chip8.disassemble(vmState, vmState.pc, 2))
                    "df" -> print(chip8.disassemble(vmState))
                    "h" -> printHelp()
                    "q" -> System.exit(0)
                    else -> printHelp()
                }
            }
        } else {
            System.exit(0)
        }
    }
}

fun printHelp() {
    val builder = StringBuilder()
    builder.line("n           ... Execute next instruction")
    builder.line("r           ... Run until the PC doesn't change anymore")
    builder.line("b <address> ... Set breakpoint at adress (hex)")
    builder.line("s           ... Display registers and stack")
    builder.line("v           ... Display VRAM contents")
    builder.line("d           ... Display disassembly around PC")
    builder.line("df          ... Display disassembly of full program")
    builder.line("q           ... Quit")
}