package no.nav.fo.veilarbregistrering.besvarelse

data class Stilling (
    val label: String? = null,

    val konseptId: Long = 0,

    val styrk08: String? = null,
) {



//    fun setLabel(label: String?): Stilling {
//        this.label = label
//        return this
//    }
//
//    fun setKonseptId(konseptId: Long): Stilling {
//        this.konseptId = konseptId
//        return this
//    }
//
//    fun setStyrk08(styrk08: String?): Stilling {
//        this.styrk08 = styrk08
//        return this
//    }

//    override fun equals(o: Any?): Boolean {
//        if (o === this) return true
//        if (o !is Stilling) return false
//        val other = o
//        if (!other.canEqual(this as Any)) return false
//        val `this$label`: Any? = label
//        val `other$label`: Any? = other.label
//        if (if (`this$label` == null) `other$label` != null else `this$label` != `other$label`) return false
//        if (konseptId != other.konseptId) return false
//        val `this$styrk08`: Any? = styrk08
//        val `other$styrk08`: Any? = other.styrk08
//        return if (if (`this$styrk08` == null) `other$styrk08` != null else `this$styrk08` != `other$styrk08`) false else true
//    }
//
//    protected fun canEqual(other: Any?): Boolean {
//        return other is Stilling
//    }
//
//    override fun hashCode(): Int {
//        val PRIME = 59
//        var result = 1
//        val `$label`: Any? = label
//        result = result * PRIME + (`$label`?.hashCode() ?: 43)
//        val `$konseptId` = konseptId
//        result = result * PRIME + (`$konseptId` ushr 32 xor `$konseptId`).toInt()
//        val `$styrk08`: Any? = styrk08
//        result = result * PRIME + (`$styrk08`?.hashCode() ?: 43)
//        return result
//    }

    override fun toString(): String {
        return "Stilling(label=" + label + ", konseptId=" + konseptId + ", styrk08=" + styrk08 + ")"
    }

    companion object {
        @JvmStatic
        fun tomStilling(): Stilling {
            return Stilling("", -1L, "-1")
        }

        @JvmStatic
        fun ingenYrkesbakgrunn(): Stilling {
            return Stilling("X", -1L, "X")
        }
    }
}