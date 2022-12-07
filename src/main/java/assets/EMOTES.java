package assets;

public enum EMOTES {
    RDG("<:rubberDuckyGreen:854040000902201384>"),
    RDR("<:rubberDuckyRed:854040013284180028>"),
    RDSuccess("<:Success:988039759337259008>"),
    RDError("<:Error:988039773711118357>"),
    RDBlacklisted("<:Blacklisted:988039788860960818>"),
    RDCoolDown("<:Cooldown:988039803222233128>"),
    RDLackingPermissions("<:LackingPermissions:988040009686855771>"),
    RDChannelWhitelist("<:ChannelNotWhitelisted:988040025792995388>"),
    RDServerWhitelist("<:ServerNotWhitelisted:988040042402443284>"),
    NLD("<:NotLikeDis:919314084552376410>"),
    // Progressbar left
    L0("<:left00:854039658374496256>"),
    L1("<:left01:854039645796433951>"),
    L2("<:left02:854039632798154763>"),
    L3("<:left03:854039622291030026>"),
    L4("<:left04:854039612955164682>"),
    L5("<:left05:854039602675712000>"),
    L6("<:left06:854039593707634688>"),
    L7("<:left07:854039583309168712>"),
    L8("<:left08:854039570676187178>"),
    L9("<:left09:854039561381871617>"),
    L10("<:left10:854039549918445588>"),
    // Progressbar middle
    M0("<:middle00:855781876757430292>"),
    M1("<:middle01:855781865398730782>"),
    M2("<:middle02:855781849837731871>"),
    M3("<:middle03:855781838252146703>"),
    M4("<:middle04:855781825812496414>"),
    M5("<:middle05:855781810621513728>"),
    M6("<:middle06:855781795119235093>"),
    M7("<:middle07:855781783383965706>"),
    M8("<:middle08:855781764566745149>"),
    M9("<:middle09:855781736386396180>"),
    M10("<:middle10:855781713598742539>"),
    // Progressbar right
    R0("<:right00:854039372727189534>"),
    R1("<:right01:854039358885855253>"),
    R2("<:right02:854039346540445746>"),
    R3("<:right03:854039333694865468>"),
    R4("<:right04:854039317228027915>"),
    R5("<:right05:854039299790733323>"),
    R6("<:right06:854039228774481950>"),
    R7("<:right07:854039214986100736>"),
    R8("<:right08:854039198893604884>"),
    R9("<:right09:854039183755968552>"),
    R10("<:right10:854039167956287488>");

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
