// vim: tabstop=2 expandtab shiftwidth=2 softtabstop=2
package regs_router
import chisel3._
import chisel3.util._
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.jtag._

object RegsMerger {
  def merge(output: RegisterPorts, inputs: Seq[RegisterPorts]) {
      output.ra := inputs(0).ra | inputs(1).ra
      output.sp := inputs(0).sp | inputs(1).sp
      output.gp := inputs(0).gp | inputs(1).gp
      output.tp := inputs(0).tp | inputs(1).tp
      output.t0 := inputs(0).t0 | inputs(1).t0
      output.t1 := inputs(0).t1 | inputs(1).t1
      output.t2 := inputs(0).t2 | inputs(1).t2
      output.fp := inputs(0).fp | inputs(1).fp
      output.s1 := inputs(0).s1 | inputs(1).s1
      output.a0 := inputs(0).a0 | inputs(1).a0
      output.a1 := inputs(0).a1 | inputs(1).a1
      output.a2 := inputs(0).a2 | inputs(1).a2
      output.a3 := inputs(0).a3 | inputs(1).a3
      output.a4 := inputs(0).a4 | inputs(1).a4
      output.a5 := inputs(0).a5 | inputs(1).a5
      output.a6 := inputs(0).a6 | inputs(1).a6
      output.a7 := inputs(0).a7 | inputs(1).a7
      output.s2 := inputs(0).s2 | inputs(1).s2
      output.s3 := inputs(0).s3 | inputs(1).s3
      output.s4 := inputs(0).s4 | inputs(1).s4
      output.s5 := inputs(0).s5 | inputs(1).s5
      output.s6 := inputs(0).s6 | inputs(1).s6
      output.s7 := inputs(0).s7 | inputs(1).s7
      output.s8 := inputs(0).s8 | inputs(1).s8
      output.s9 := inputs(0).s9 | inputs(1).s9
      output.s10 := inputs(0).s10 | inputs(1).s10
      output.s11 := inputs(0).s11 | inputs(1).s11
      output.t3 := inputs(0).t3 | inputs(1).t3
      output.t4 := inputs(0).t4 | inputs(1).t4
      output.t5 := inputs(0).t5 | inputs(1).t5
      output.t6 := inputs(0).t6 | inputs(1).t6
      output.pc := inputs(0).pc | inputs(1).pc
      output.interrupt := inputs(0).interrupt | inputs(1).interrupt
      output.interrupt_cause := inputs(0).interrupt_cause | inputs(1).interrupt_cause
      output.time := inputs(0).time | inputs(1).time
      output.debug := inputs(0).debug | inputs(1).debug
      output.isa := inputs(0).isa | inputs(1).isa
      output.sd := inputs(0).sd | inputs(1).sd
      output.sd_rv32 := inputs(0).sd_rv32 | inputs(1).sd_rv32
      output.mpp := inputs(0).mpp | inputs(1).mpp
      output.spp := inputs(0).spp | inputs(1).spp
      output.mpie := inputs(0).mpie | inputs(1).mpie
      output.mie := inputs(0).mie | inputs(1).mie
  }
}

class RegsRouter(cpu_count:Int) extends Module {

  val io = IO(new Bundle{
    val selector = Input(UInt(3.W))
    val input_regs = Vec(cpu_count, Flipped(new RegisterPorts))
    val output_regs = Vec(cpu_count, new RegisterPorts)
  })

  val regs_to_cpu0 = 1.U
  val regs_to_cpu1 = 2.U
  val regs_to_cpu2 = 4.U
  when(io.selector === regs_to_cpu0) {
    /* route regs from cpu 1 and 2 to cpu 0 */
    RegsMerger.merge(io.output_regs(0), Seq(io.input_regs(1), io.input_regs(2)))
    RegsMerger.merge(io.output_regs(1), Seq(io.input_regs(1), io.input_regs(1)))
    RegsMerger.merge(io.output_regs(2), Seq(io.input_regs(2), io.input_regs(2)))
  }.elsewhen(io.selector === regs_to_cpu1) {
    /* route regs from cpu 0 and 2 to cpu 1 */
    RegsMerger.merge(io.output_regs(0), Seq(io.input_regs(0), io.input_regs(0)))
    RegsMerger.merge(io.output_regs(1), Seq(io.input_regs(0), io.input_regs(2)))
    RegsMerger.merge(io.output_regs(2), Seq(io.input_regs(2), io.input_regs(2)))
  }.elsewhen(io.selector === regs_to_cpu2) {
    /* route regs from cpu 0 and 1 to cpu 2 */
    RegsMerger.merge(io.output_regs(0), Seq(io.input_regs(0), io.input_regs(0)))
    RegsMerger.merge(io.output_regs(1), Seq(io.input_regs(1), io.input_regs(1)))
    RegsMerger.merge(io.output_regs(2), Seq(io.input_regs(0), io.input_regs(1)))
  }.otherwise{
    RegsMerger.merge(io.output_regs(0), Seq(io.input_regs(0), io.input_regs(0)))
    RegsMerger.merge(io.output_regs(1), Seq(io.input_regs(1), io.input_regs(1)))
    RegsMerger.merge(io.output_regs(2), Seq(io.input_regs(2), io.input_regs(2)))
  }

}

object RegsRouter extends App {
  if(args.length == 0) {
    chisel3.Driver.execute(args, () => new RegsRouter(3))
  } else {
    iotesters.Driver.execute(args, () => new RegsRouter(3)){ c => new RegsRouterTests(c) }
  }
}
