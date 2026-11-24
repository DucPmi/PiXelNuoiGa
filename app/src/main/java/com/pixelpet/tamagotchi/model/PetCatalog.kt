package com.pixelpet.tamagotchi.model

enum class SilhouetteType {
    CHICK, CAT, DOG, DRAGON, DINO,
    RABBIT, FOX, PANDA, PENGUIN, SHARK,
    TURTLE, SQUIRREL, OWL, FROG, BEE,
    UNICORN, OCTOPUS, SHEEP, HEDGEHOG, SLIME
}

data class PetSpecies(
    val id: String,
    val nameVi: String,
    val silhouette: SilhouetteType,
    val primaryColor: Int,
    val secondaryColor: Int,
    val accentColor: Int,
    val stageNames: List<String>
)

object PetCatalog {
    val allSpecies: List<PetSpecies> = listOf(
        PetSpecies("chick", "Gà Con", SilhouetteType.CHICK, 0xFFFACC15.toInt(), 0xFFFEF08A.toInt(), 0xFFF97316.toInt(),
            listOf("Trứng Gà Vàng", "Gà Bông Sơ Sinh", "Gà Chip Năng Động", "Gà Trống Tươi", "Phượng Hoàng Gà")),
        PetSpecies("cat", "Mèo Mướp", SilhouetteType.CAT, 0xFFFB923C.toInt(), 0xFFFFEDD5.toInt(), 0xFFF43F5E.toInt(),
            listOf("Trứng Mèo Cam", "Mèo Con Meo", "Mèo Tinh Nghịch", "Mèo Mướp Béo", "Mèo Thần Tài")),
        PetSpecies("dog", "Cún Corgi", SilhouetteType.DOG, 0xFFF59E0B.toInt(), 0xFFFFFBEB.toInt(), 0xFFEF4444.toInt(),
            listOf("Trứng Cún Bông", "Cún Sữa", "Corgi Chân Ngắn", "Corgi Siêu Tốc", "Corgi Hoàng Gia")),
        PetSpecies("dragon", "Rồng Lửa", SilhouetteType.DRAGON, 0xFFEF4444.toInt(), 0xFFFDE047.toInt(), 0xFFF97316.toInt(),
            listOf("Trứng Rồng Đỏ", "Rồng Tí Hon", "Rồng Phun Tia", "Hỏa Long", "Rồng Thần Rực Lửa")),
        PetSpecies("dino", "Khủng Long", SilhouetteType.DINO, 0xFF22C55E.toInt(), 0xFFBBF7D0.toInt(), 0xFF16A34A.toInt(),
            listOf("Trứng Cổ Đại", "Dino Xanh Non", "Dino Nhảy Nhót", "T-Rex Vui Tính", "Vua Khủng Long")),
        PetSpecies("rabbit", "Thỏ Ngọc", SilhouetteType.RABBIT, 0xFFF9A8D4.toInt(), 0xFFFFF1F2.toInt(), 0xFFEC4899.toInt(),
            listOf("Trứng Hồng Phấn", "Thỏ Bông Nhỏ", "Thỏ Tai Dài", "Thỏ Cà Rốt", "Thỏ Ngọc Cung Trăng")),
        PetSpecies("fox", "Cáo Lửa", SilhouetteType.FOX, 0xFFF97316.toInt(), 0xFFFFFFFF.toInt(), 0xFFDC2626.toInt(),
            listOf("Trứng Cáo Đỏ", "Cáo Con", "Cáo Rừng Xanh", "Cáo Thông Thái", "Hồ Ly Chín Đuôi")),
        PetSpecies("panda", "Gấu Trúc", SilhouetteType.PANDA, 0xFFF8FAFC.toInt(), 0xFF334155.toInt(), 0xFF22C55E.toInt(),
            listOf("Trứng Trúc Xanh", "Gấu Mầm Non", "Gấu Mê Tre", "Gấu Trúc Kungfu", "Đại Sư Gấu Trúc")),
        PetSpecies("penguin", "Cánh Cụt", SilhouetteType.PENGUIN, 0xFF38BDF8.toInt(), 0xFFF8FAFC.toInt(), 0xFFFB923C.toInt(),
            listOf("Trứng Băng Xanh", "Cánh Cụt Bông", "Cánh Cụt Trượt", "Cánh Cụt Bảnh", "Hoàng Đế Nam Cực")),
        PetSpecies("shark", "Cá Mập Mini", SilhouetteType.SHARK, 0xFF0EA5E9.toInt(), 0xFFE0F2FE.toInt(), 0xFF0284C7.toInt(),
            listOf("Trứng Sóng Biển", "Cá Mập Sữa", "Cá Mập Lướt Sóng", "Cá Mập Xanh", "Chúa Tể Đại Dương")),
        PetSpecies("turtle", "Rùa Biển", SilhouetteType.TURTLE, 0xFF10B981.toInt(), 0xFFA7F3D0.toInt(), 0xFFF59E0B.toInt(),
            listOf("Trứng Mai Ngọc", "Rùa Tí Xíu", "Rùa Bơi Nhanh", "Rùa Mai Vàng", "Thần Quy Biển Cả")),
        PetSpecies("squirrel", "Sóc Bay", SilhouetteType.SQUIRREL, 0xFFD97706.toInt(), 0xFFFEF3C7.toInt(), 0xFF92400E.toInt(),
            listOf("Trứng Hạt Dẻ", "Sóc Bông", "Sóc Chuyền Cành", "Sóc Bay Lượn", "Sóc Tốc Độ Ánh Sáng")),
        PetSpecies("owl", "Cú Mèo", SilhouetteType.OWL, 0xFFA855F7.toInt(), 0xFFF3E8FF.toInt(), 0xFFFACC15.toInt(),
            listOf("Trứng Sao Tím", "Cú Tròn Mắt", "Cú Học Giả", "Cú Ánh Trăng", "Hiền Triết Cú Mèo")),
        PetSpecies("frog", "Ếch Xanh", SilhouetteType.FROG, 0xFF4ADE80.toInt(), 0xFFDCFCE7.toInt(), 0xFFF43F5E.toInt(),
            listOf("Trứng Lá Sen", "Nòng Nọc Vui", "Ếch Nhảy Cao", "Ếch Mưa Rào", "Hoàng Tử Ếch")),
        PetSpecies("bee", "Ong Vàng", SilhouetteType.BEE, 0xFFFACC15.toInt(), 0xFF1E293B.toInt(), 0xFF38BDF8.toInt(),
            listOf("Trứng Mật Hoa", "Ong Bụ Bẫm", "Ong Chăm Chỉ", "Ong Mật Vàng", "Nữ Hoàng Ong")),
        PetSpecies("unicorn", "Kỳ Lân", SilhouetteType.UNICORN, 0xFFE879F9.toInt(), 0xFFFFFFFF.toInt(), 0xFF38BDF8.toInt(),
            listOf("Trứng Cầu Vồng", "Ngựa Sao Nhỏ", "Kỳ Lân Hồng", "Kỳ Lân Ánh Sáng", "Thánh Thú Cầu Vồng")),
        PetSpecies("octopus", "Bạch Tuộc", SilhouetteType.OCTOPUS, 0xFFFB7185.toInt(), 0xFFFFE4E6.toInt(), 0xFFF59E0B.toInt(),
            listOf("Trứng San Hô", "Tuộc Nhí", "Tuộc 8 Tay", "Tuộc Nhạc Công", "Bạch Tuộc Hoàng Kim")),
        PetSpecies("sheep", "Cừu Bông", SilhouetteType.SHEEP, 0xFFFFFFFF.toInt(), 0xFFFDE68A.toInt(), 0xFF60A5FA.toInt(),
            listOf("Trứng Mây Trắng", "Cừu Sữa", "Cừu Kẹo Bông", "Cừu Đám Mây", "Cừu Thiên Đường")),
        PetSpecies("hedgehog", "Nhím Xù", SilhouetteType.HEDGEHOG, 0xFF94A3B8.toInt(), 0xFFFFF7ED.toInt(), 0xFF3B82F6.toInt(),
            listOf("Trứng Gai Mềm", "Nhím Hạt Tiêu", "Nhím Lăn Tròn", "Nhím Siêu Thanh", "Nhím Tia Chớp")),
        PetSpecies("slime", "Slime Sao", SilhouetteType.SLIME, 0xFF2DD4BF.toInt(), 0xFFCCFBF1.toInt(), 0xFFFACC15.toInt(),
            listOf("Trứng Thạch Xanh", "Slime Giọt Sương", "Slime Nảy Bật", "Slime Pha Lê", "Vua Slime Ngân Hà"))
    )

    fun findById(id: String): PetSpecies {
        return allSpecies.firstOrNull { it.id == id } ?: allSpecies.first()
    }
}
