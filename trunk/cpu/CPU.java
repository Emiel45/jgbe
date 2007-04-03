public class CPU
{
    private static final int CARRY8b    = 512;
    private static final int CARRY8b_SHR = 5;

    //FLAGS
        private static final int FLAG_REG = 5;
        private static final int ZF_Shift = 7;
        private static final int NF_Shift = ZF_Shift - 1;
        private static final int HC_Shift = NF_Shift - 1;
    private static final int ZF_Mask  = 1 << ZF_Shift;
    private static final int NF_Mask  = 1 << NF_Shift;
    private static final int HC_Mask  = 1 << HC_Shift;

    // Half carry van de 8b regs
    private static final int HALF_CARRY8b = 16;
    private static final int HALF_CARRY8b_SHL = 2;

    private int[] regs = new int[8]; //[A,B,C,D,E,F,H,L]
    private static final int A = 0;
    private static final int B = 1;
    private static final int C = 2;
    private static final int D = 3;
    private static final int E = 4;
    private static final int F = FLAG_REG;
    private static final int H = 6;
    private static final int L = 7;

    private int PC;

    private void printCPUstatus()
    {
        System.out.println("--- CPU STATUS ---");
        System.out.println("  --- REGS ---");
        System.out.println("    A  = " + regs[A]);
        System.out.println("    B  = " + regs[B]);
        System.out.println("    C  = " + regs[C]);
        System.out.println("    D  = " + regs[D]);
        System.out.println("    E  = " + regs[E]);
        System.out.println("    F  = " + regs[F]);
        System.out.println("    H  = " + regs[H]);
        System.out.println("    L  = " + regs[L]);
        System.out.println("    PC = " + PC);
        System.out.println("  --- FLAGS ---");
        System.out.println("    Z  = " + ((regs[FLAG_REG] & (1 << 7)) == (1 << 7)));
        System.out.println("    n  = " + ((regs[FLAG_REG] & (1 << 6)) == (1 << 6)));
        System.out.println("    h  = " + ((regs[FLAG_REG] & (1 << 5)) == (1 << 5)));
        System.out.println("    C  = " + ((regs[FLAG_REG] & (1 << 4)) == (1 << 4)));
        System.out.println("    F3 = " + ((regs[FLAG_REG] & (1 << 3)) == (1 << 3)));
        System.out.println("    F2 = " + ((regs[FLAG_REG] & (1 << 2)) == (1 << 2)));
        System.out.println("    F1 = " + ((regs[FLAG_REG] & (1 << 1)) == (1 << 1)));
        System.out.println("    F0 = " + ((regs[FLAG_REG] & (1 << 0)) == (1 << 0)));
        System.out.println("------------------");
    }

    private void inc8b(int reg_index)
    {
        // CHECK FOR VIOLATIONS
        if (reg_index == F)
        {
            System.out.println("INCREASING F!");
        }

        // Clear & Set HC
        regs[FLAG_REG] = regs[FLAG_REG] & ~HC_Mask;
        regs[FLAG_REG] = regs[FLAG_REG] | ((((regs[reg_index] & 0xF) + 1) & 0x10) << 1);

                //Update register
        regs[reg_index] = (++regs[reg_index] & 0xFF);

        // clear & set ZF
        regs[FLAG_REG] = regs[FLAG_REG] & ~ZF_Mask;
        regs[FLAG_REG] = regs[FLAG_REG] | (((regs[reg_index]==0)?1:0)<<ZF_Shift);

        // clear & set NF
        regs[FLAG_REG] = regs[FLAG_REG] & ~NF_Mask;
//        System.out.println("reg["+reg_index+"]="+regs[reg_index]+" ZF="+((regs[FLAG_REG]&ZF_Mask)>>ZF_Shift)+" HC="+((regs[FLAG_REG]&HC_Mask)>>HC_Shift)+" NF="+((regs[FLAG_REG]&NF_Mask)>>NF_Shift));
    }

    private void dec8b(int reg_index)
    {
        // hcb: half carry before increase
        int hcb = regs[reg_index] & HALF_CARRY8b;
        regs[reg_index] = (--regs[reg_index] & 0xFF);
        // hcb: half carry after increase
        int hca = regs[reg_index] & HALF_CARRY8b;
        // Set halfcarry flag
        regs[FLAG_REG] = regs[FLAG_REG] | ((hca ^hcb) << HALF_CARRY8b_SHL);
        // clear ZF
        regs[FLAG_REG] = regs[FLAG_REG] & ~ZF_Mask;
        // Set Z flag
        regs[FLAG_REG] = regs[FLAG_REG] | ((regs[reg_index]==0)?1:0<<ZF_Mask);
        // SET n flag
        regs[FLAG_REG] = regs[FLAG_REG] | NF_Mask;
    }

    private void inc16b()
    {
    }

    private void JPnn(int nn)
    {
        PC = nn;
    }

    private void JPccnn(boolean cc, int nn)
    {
        if (cc) JPnn(nn);
    }

    private void JRe(int e)
    {
        PC += e;
    }

    private void JRcce(boolean cc, int e)
    {
        if (cc) JRe(e);
    }

    private int fetch()
    {
        return 0x00004;
    }

    private boolean excecute(int instr)
    {
        switch(instr)
        {
            case 0x0000:
                // NOP
                break;
            case 0x0001:
                // LD BC,&0000
                // TODO
                break;
            case 0x0002:
                // LD (BC),A
                // TODO
                break;
            case 0x0003:
                // INC BC
                // TODO
                break;
            case 0x0004:
                // INC B
                inc8b(B);
                break;
            case 0x0005:
                // DEC B
                dec8b(B);
                break;
            default:
                System.out.println("UNKNOWN INSTRUCTION: " + instr);
                return false;
        }

        return true;
    }

    private boolean inc8b_diag() {
        System.out.println(F);

        /***************************************************************************************************************
        * Test INC_8b
        * Tests 0x00 + 1, 0x0f + 1, 0xff + 1 for setting AND clearing of flags
        */
        boolean status=true;
        regs[A]=0;
        regs[FLAG_REG] = 0; // clear all flags
        inc8b(A);
        if(regs[A]!=1) {
            System.out.println("Error: 0 + 1 != 1");
            status = status && false;
            }
        if((regs[FLAG_REG]&ZF_Mask) == ZF_Mask) {
            System.out.println("Error: INC8b: A:0->1 and ZF is set");
            status = status && false;
            }
        if((regs[FLAG_REG]&HC_Mask) == HC_Mask) {
            System.out.println("Error: INC8b: A:0->1 and HC is set");
            status = status && false;
            }
        if((regs[FLAG_REG]&NF_Mask) == NF_Mask) {
            System.out.println("Error: INC8b: Inc'd and NF not set");
            status = status && false;
            }

        regs[A]=0;
        regs[FLAG_REG] = 0xff; // set all flags
        inc8b(A);
        if(regs[A]!=1) {
            System.out.println("Error: 0 + 1 != 1");
            status = status && false;
            }
        if((regs[FLAG_REG]&ZF_Mask) == ZF_Mask) {
            System.out.println("Error: INC8b: A:0->1 and ZF is set");
            status = status && false;
            }
        if((regs[FLAG_REG]&HC_Mask) == HC_Mask) {
            System.out.println("Error: INC8b: A:0->1 and HC is set");
            status = status && false;
            }
        if((regs[FLAG_REG]&NF_Mask) == NF_Mask) {
            System.out.println("Error: INC8b: Inc'd and NF not set");
            status = status && false;
            }

        regs[A]=0x0f;
        regs[FLAG_REG] = 0; // clear all flags
        inc8b(A);
        if(regs[A]!=0x10) {
            System.out.println("Error: 0x0f + 1 != 0x10");
            status = status && false;
            }
        if((regs[FLAG_REG]&ZF_Mask) == ZF_Mask) {
            System.out.println("Error: INC8b: A:0x0f->0x10 and ZF is set");
            status = status && false;
            }
        if((regs[FLAG_REG]&HC_Mask) != HC_Mask) {
            System.out.println("Error: INC8b: A:0x0f->0x10 and HC is NOT set");
            status = status && false;
            }
        if((regs[FLAG_REG]&NF_Mask) == NF_Mask) {
            System.out.println("Error: INC8b: Inc'd and NF not set");
            status = status && false;
            }

        regs[A]=0x0f;
        regs[FLAG_REG] = 0xff; // set all flags
        inc8b(A);
        if(regs[A]!=0x10) {
            System.out.println("Error: 0x0f + 1 != 0x10");
            status = status && false;
            }
        if((regs[FLAG_REG]&ZF_Mask) == ZF_Mask) {
            System.out.println("Error: INC8b: A:0x0f->0x10 and ZF is set");
            status = status && false;
            }
        if((regs[FLAG_REG]&HC_Mask) != HC_Mask) {
            System.out.println("Error: INC8b: A:0x0f->0x10 and HC is NOT set");
            status = status && false;
            }
        if((regs[FLAG_REG]&NF_Mask) == NF_Mask) {
            System.out.println("Error: INC8b: Inc'd and NF not set");
            status = status && false;
            }

        regs[A]=0xff;
        regs[FLAG_REG] = 0; // clear all flags
        inc8b(A);
        if(regs[A]!=0x00) {
            System.out.println("Error: 0xff + 1 != 0x00");
            status = status && false;
            }
        if((regs[FLAG_REG]&ZF_Mask) != ZF_Mask) {
            System.out.println("Error: INC8b: A:0xff->0x00 and ZF is NOT set");
            status = status && false;
            }
        if((regs[FLAG_REG]&HC_Mask) != HC_Mask) {
            System.out.println("Error: INC8b: A:0xff->0x00 and HC is NOT set");
            status = status && false;
            }
        if((regs[FLAG_REG]&NF_Mask) == NF_Mask) {
            System.out.println("Error: INC8b: Inc'd and NF not set");
            status = status && false;
            }

        regs[A]=0xff;
        regs[FLAG_REG] = 0xff; // set all flags
        inc8b(A);
        if(regs[A]!=0x00) {
            System.out.println("Error: 0xff + 1 != 0x00");
            status = status && false;
            }
        if((regs[FLAG_REG]&ZF_Mask) != ZF_Mask) {
            System.out.println("Error: INC8b: A:0xff->0x00 and ZF is NOT set");
            status = status && false;
            }
        if((regs[FLAG_REG]&HC_Mask) != HC_Mask) {
            System.out.println("Error: INC8b: A:0xff->0x00 and HC is NOT set");
            status = status && false;
            }
        if((regs[FLAG_REG]&NF_Mask) == NF_Mask) {
            System.out.println("Error: INC8b: Inc'd and NF not set");
            status = status && false;
            }
        return status;
    }
    private int diagnose(boolean verbose) {
            boolean result;
            int count=0;
            result = inc8b_diag();
      if(verbose && result) {
        System.out.println("INC8b instruction appears to work ok");
        }
      else {
        System.out.println("*ERROR* IN INC8b INSTRUCTION!");
        ++count;
        }
      if(verbose || count>0) System.out.println("There were errors in "+count+"instructions");

    printCPUstatus();

      return count;
    }

    public static final void main(String[] args)
    {
        (new CPU()).diagnose(true);
    }
}