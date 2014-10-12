package chip8;

class Disassembler(): Decoder {
    val builder = StringBuilder()
    override fun before(opCode: Int, address: Int) {
        builder.append("addr: 0x${address.toHex()}, op: 0x${opCode.toHex()}, ")
    }
    override fun unknown(opCode: Int, address: Int) {
        builder.append("Unknown opcode addr: 0x${address.toHex()}, op: 0x${opCode.toHex()}")
    }
    override fun clear() {
        builder.line("clear")
    }
    override fun ret() {
        builder.line("ret")
    }
    override fun jmp(address: Int) {
        builder.line("jmp 0x${address.toHex()}")
    }
    override fun call(address: Int) {
        builder.line("call 0x${address.toHex()}")
    }
    override fun jeq(reg: Int, value: Int) {
        builder.line("jeq v${reg.toHex()}, 0x${value.toHex()}")
    }
    override fun jneq(reg: Int, value: Int) {
        builder.line("jneq v${reg.toHex()}, 0x${value.toHex()}")
    }
    override fun jeqr(reg1: Int, reg2: Int) {
        builder.line("jeqr v${reg1.toHex()}, v${reg2.toHex()}")
    }
    override fun set(reg: Int, value: Int) {
        builder.line("set v${reg.toHex()}, 0x${value.toHex()}")
    }
    override fun add(reg: Int, value: Int) {
        builder.line("add v${reg.toHex()}, 0x${value.toHex()}")
    }
    override fun setr(reg1: Int, reg2: Int) {
        builder.line("setr v${reg1.toHex()}, v${reg2.toHex()}")
    }
    override fun or(reg1: Int, reg2: Int) {
        builder.line("or v${reg1.toHex()}, v${reg2.toHex()}")
    }
    override fun and(reg1: Int, reg2: Int) {
        builder.line("and v${reg1.toHex()}, v${reg2.toHex()}")
    }
    override fun xor(reg1: Int, reg2: Int) {
        builder.line("xor v${reg1.toHex()}, v${reg2.toHex()}")
    }
    override fun addr(reg1: Int, reg2: Int) {
        builder.line("addr v${reg1.toHex()}, v${reg2.toHex()}")
    }
    override fun sub(reg1: Int, reg2: Int) {
        builder.line("sub v${reg1.toHex()}, v${reg2.toHex()}")
    }
    override fun shr(reg1: Int) {
        builder.line("shr v${reg1.toHex()}")
    }
    override fun subb(reg1: Int, reg2: Int) {
        builder.line("subb v${reg1.toHex()}, v${reg2.toHex()}")
    }
    override fun shl(reg1: Int) {
        builder.line("shl v${reg1.toHex()}")
    }
    override fun jneqr(reg1: Int, reg2: Int) {
        builder.line("jneqr v${reg1.toHex()}, v${reg2.toHex()}")
    }
    override fun seti(value: Int) {
        builder.line("seti 0x${value.toHex()}")
    }
    override fun jmpv0(address: Int) {
        builder.line("jmpv0 0x${address.toHex()}")
    }
    override fun rand(reg: Int, value: Int) {
        builder.line("rand v${reg.toHex()}, 0x${value.toHex()}")
    }
    override fun draw(reg1: Int, reg2: Int, value: Int) {
        builder.line("draw v${reg1.toHex()}, v${reg2.toHex()}, 0x${value.toHex()}")
    }
    override fun jkey(reg: Int) {
        builder.line("jkey v${reg.toHex()}")
    }
    override fun jnkey(reg: Int) {
        builder.line("jnkey v${reg.toHex()}")
    }
    override fun getdelay(reg: Int) {
        builder.line("getdelay v${reg.toHex()}")
    }
    override fun waitkey(reg: Int) {
        builder.line("waitkey v${reg.toHex()}")
    }
    override fun setdelay(reg: Int) {
        builder.line("setdelay v${reg.toHex()}")
    }
    override fun setsound(reg: Int) {
        builder.line("setsound v${reg.toHex()}")
    }
    override fun addi(reg: Int) {
        builder.line("addi v${reg.toHex()}")
    }
    override fun spritei(reg: Int) {
        builder.line("spritei v${reg.toHex()}")
    }
    override fun bcd(reg: Int) {
        builder.line("bcd v${reg.toHex()}")
    }
    override fun push(reg: Int) {
        builder.line("push v0-v${reg.toHex()}")
    }
    override fun pop(reg: Int) {
        builder.line("pop v0-v${reg.toHex()}")
    }

    override fun toString(): String {
        return builder.toString()
    }
}

fun disassemble(vmState: VmState): String {
    val decoder = Disassembler()
    for(addr in 0x200..(0x200+vmState.programSize - 1) step 2) {
        val msb = vmState.ram[addr]
        val lsb = vmState.ram[addr + 1]
        decode(decoder, addr, msb, lsb)
    }
    return decoder.toString()
}