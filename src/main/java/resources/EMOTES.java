package resources;

public enum EMOTES {
    RDG(":rubberDuckyGreen:854040000902201384"),
    RDR(":rubberDuckyRed:854040013284180028"),
    NLD(":NotLikeDis:854040049691131924"),
    // Progressbar left
    L0(":left00:854039658374496256"),
    L1(":left01:854039645796433951"),
    L2(":left02:854039632798154763"),
    L3(":left03:854039622291030026"),
    L4(":left04:854039612955164682"),
    L5(":left05:854039602675712000"),
    L6(":left06:854039593707634688"),
    L7(":left07:854039583309168712"),
    L8(":left08:854039570676187178"),
    L9(":left09:854039561381871617"),
    L10(":left10:854039549918445588"),
    // Progressbar middle
    M0(":middle00:854039535730032700"),
    M1(":middle01:854039524261494784"),
    M2(":middle02:854039512975933482"),
    M3(":middle03:854039502947483709"),
    M4(":middle04:854039487688081451"),
    M5(":middle05:854039476077330521"),
    M6(":middle06:854039464266301480"),
    M7(":middle07:854039449893994546"),
    M8(":middle08:854039437960282132"),
    M9(":middle09:854039422810587159"),
    M10(":middle10:854039397992235069"),
    // Progressbar right
    R0(":right00:854039372727189534"),
    R1(":right01:854039358885855253"),
    R2(":right02:854039346540445746"),
    R3(":right03:854039333694865468"),
    R4(":right04:854039317228027915"),
    R5(":right05:854039299790733323"),
    R6(":right06:854039228774481950"),
    R7(":right07:854039214986100736"),
    R8(":right08:854039198893604884"),
    R9(":right09:854039183755968552"),
    R10(":right10:854039167956287488");

    private final String id;

    private EMOTES(String id) {
        this.id = id;
    }

    public String getAsReaction() {
        return this.id;
    }

    public String getAsEmote() {
        return "<" + this.id + ">";
    }
}
