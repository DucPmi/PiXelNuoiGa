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
        PetSpecies(
            id = "chick",
            nameVi = "Gà Con",
            silhouette = SilhouetteType.CHICK,
            primaryColor = 0xFFFACC15.toInt(),
            secondaryColor = 0xFFFEF08A.toInt(),
            accentColor = 0xFFF97316.toInt(),
            stageNames = listOf("Trứng Gà Vàng", "Gà Bông Sơ Sinh", "Gà Chip Năng Động", "Gà Trống Tươi", "Phượng Hoàng Gà")
        ),
        PetSpecies(
            id = "cat",
            nameVi = "Mèo Mướp",
            silhouette = SilhouetteType.CAT,
            primaryColor = 0xFFFB923C.toInt(),
            secondaryColor = 0xFFFFEDD5.toInt(),
            accentColor = 0xFFF43F5E.toInt(),
            stageNames = listOf("Trứng Mèo Cam", "Mèo Con Meo", "Mèo Tinh Nghịch", "Mèo Mướp Béo", "Mèo Thần Tài")
        ),
        PetSpecies(
            id = "dog",
            nameVi = "Cún Corgi",
            silhouette = SilhouetteType.DOG,
            primaryColor = 0xFFF59E0B.toInt(),
            secondaryColor = 0xFFFFFBEB.toInt(),
            accentColor = 0xFFEF4444.toInt(),
            stageNames = listOf("Trứng Cún Bông", "Cún Sữa", "Corgi Chân Ngắn", "Corgi Siêu Tốc", "Corgi Hoàng Gia")
        ),
        PetSpecies(
            id = "dragon",
            nameVi = "Rồng Lửa",
            silhouette = SilhouetteType.DRAGON,
            primaryColor = 0xFFEF4444.toInt(),
            secondaryColor = 0xFFFDE047.toInt(),
            accentColor = 0xFFF97316.toInt(),
            stageNames = listOf("Trứng Rồng Đỏ", "Rồng Tí Hon", "Rồng Phun Tia", "Hỏa Long", "Rồng Thần Rực Lửa")
        ),
        PetSpecies(
            id = "dino",
            nameVi = "Khủng Long",
            silhouette = SilhouetteType.DINO,
            primaryColor = 0xFF22C55E.toInt(),
            secondaryColor = 0xFFBBF7D0.toInt(),
            accentColor = 0xFF16A34A.toInt(),
            stageNames = listOf("Trứng Cổ Đại", "Dino Xanh Non", "Dino Nhảy Nhót", "T-Rex Vui Tính", "Vua Khủng Long")
        ),
        PetSpecies(
            id = "rabbit",
            nameVi = "Thỏ Ngọc",
            silhouette = SilhouetteType.RABBIT,
            primaryColor = 0xFFF9A8D4.toInt(),
            secondaryColor = 0xFFFFF1F2.toInt(),
            accentColor = 0xFFEC4899.toInt(),
            stageNames = listOf("Trứng Hồng Phấn", "Thỏ Bông Nhỏ", "Thỏ Tai Dài", "Thỏ Cà Rốt", "Thỏ Ngọc Cung Trăng")
        ),
        PetSpecies(
            id = "fox",
            nameVi = "Cáo Lửa",
            silhouette = SilhouetteType.FOX,
            primaryColor = 0xFFF97316.toInt(),
            secondaryColor = 0xFFFFFFFF.toInt(),
            accentColor = 0xFFDC2626.toInt(),
            stageNames = listOf("Trứng Cáo Đỏ", "Cáo Con", "Cáo Rừng Xanh", "Cáo Thông Thái", "Hồ Ly Chín Đuôi")
        ),
        PetSpecies(
            id = "panda",
            nameVi = "Gấu Trúc",
            silhouette = SilhouetteType.PANDA,
            primaryColor = 0xFFF8FAFC.toInt(),
            secondaryColor = 0xFF334155.toInt(),
            accentColor = 0xFF22C55E.toInt(),
            stageNames = listOf("Trứng Trúc Xanh", "Gấu Mầm Non", "Gấu Mê Tre", "Gấu Trúc Kungfu", "Đại Sư Gấu Trúc")
        ),
        PetSpecies(
            id = "penguin",
            nameVi = "Cánh Cụt",
            silhouette = SilhouetteType.PENGUIN,
            primaryColor = 0xFF38BDF8.toInt(),
            secondaryColor = 0xFFF8FAFC.toInt(),
            accentColor = 0xFFFB923C.toInt(),
            stageNames = listOf("Trứng Băng Xanh", "Cánh Cụt Bông", "Cánh Cụt Trượt", "Cánh Cụt Bảnh", "Hoàng Đế Nam Cực")
        ),
        PetSpecies(
            id = "shark",
            nameVi = "Cá Mập Mini",
            silhouette = SilhouetteType.SHARK,
            primaryColor = 0xFF0EA5E9.toInt(),
            secondaryColor = 0xFFE0F2FE.toInt(),
            accentColor = 0xFF0284C7.toInt(),
            stageNames = listOf("Trứng Sóng Biển", "Cá Mập Sữa", "Cá Mập Lướt Sóng", "Cá Mập Xanh", "Chúa Tể Đại Dương")
        ),
        PetSpecies(
            id = "turtle",
            nameVi = "Rùa Biển",
            silhouette = SilhouetteType.TURTLE,
            primaryColor = 0xFF10B981.toInt(),
            secondaryColor = 0xFFA7F3D0.toInt(),
            accentColor = 0xFFF59E0B.toInt(),
            stageNames = listOf("Trứng Mai Ngọc", "Rùa Tí Xíu", "Rùa Bơi Nhanh", "Rùa Mai Vàng", "Thần Quy Biển Cả")
        ),
        PetSpecies(
            id = "squirrel",
            nameVi = "Sóc Bay",
            silhouette = SilhouetteType.SQUIRREL,
            primaryColor = 0xFFD97706.toInt(),
            secondaryColor = 0xFFFEF3C7.toInt(),
            accentColor = 0xFF92400E.toInt(),
            stageNames = listOf("Trứng Hạt Dẻ", "Sóc Bông", "Sóc Chuyền Cành", "Sóc Bay Lượn", "Sóc Tốc Độ Ánh Sáng")
        ),
        PetSpecies(
            id = "owl",
            nameVi = "Cú Mèo",
            silhouette = SilhouetteType.OWL,
            primaryColor = 0xFFA855F7.toInt(),
            secondaryColor = 0xFFF3E8FF.toInt(),
            accentColor = 0xFFFACC15.toInt(),
            stageNames = listOf("Trứng Sao Tím", "Cú Tròn Mắt", "Cú Học Giả", "Cú Ánh Trăng", "Hiền Triết Cú Mèo")
        ),
        PetSpecies(
            id = "frog",
            nameVi = "Ếch Xanh",
            silhouette = SilhouetteType.FROG,
            primaryColor = 0xFF4ADE80.toInt(),
            secondaryColor = 0xFFDCFCE7.toInt(),
            accentColor = 0xFFF43F5E.toInt(),
            stageNames = listOf("Trứng Lá Sen", "Nòng Nọc Vui", "Ếch Nhảy Cao", "Ếch Mưa Rào", "Hoàng Tử Ếch")
        ),
        PetSpecies(
            id = "bee",
            nameVi = "Ong Vàng",
            silhouette = SilhouetteType.BEE,
            primaryColor = 0xFFFACC15.toInt(),
            secondaryColor = 0xFF1E293B.toInt(),
            accentColor = 0xFF38BDF8.toInt(),
            stageNames = listOf("Trứng Mật Hoa", "Ong Bụ Bẫm", "Ong Chăm Chỉ", "Ong Mật Vàng", "Nữ Hoàng Ong")
        ),
        PetSpecies(
            id = "unicorn",
            nameVi = "Kỳ Lân",
            silhouette = SilhouetteType.UNICORN,
            primaryColor = 0xFFE879F9.toInt(),
            secondaryColor = 0xFFFFFFFF.toInt(),
            accentColor = 0xFF38BDF8.toInt(),
            stageNames = listOf("Trứng Cầu Vồng", "Ngựa Sao Nhỏ", "Kỳ Lân Hồng", "Kỳ Lân Ánh Sáng", "Thánh Thú Cầu Vồng")
        ),
        PetSpecies(
            id = "octopus",
            nameVi = "Bạch Tuộc",
            silhouette = SilhouetteType.OCTOPUS,
            primaryColor = 0xFFFB7185.toInt(),
            secondaryColor = 0xFFFFE4E6.toInt(),
            accentColor = 0xFFF59E0B.toInt(),
            stageNames = listOf("Trứng San Hô", "Tuộc Nhí", "Tuộc 8 Tay", "Tuộc Nhạc Công", "Bạch Tuộc Hoàng Kim")
        ),
        PetSpecies(
            id = "sheep",
            nameVi = "Cừu Bông",
            silhouette = SilhouetteType.SHEEP,
            primaryColor = 0xFFFFFFFF.toInt(),
            secondaryColor = 0xFFFDE68A.toInt(),
            accentColor = 0xFF60A5FA.toInt(),
            stageNames = listOf("Trứng Mây Trắng", "Cừu Sữa", "Cừu Kẹo Bông", "Cừu Đám Mây", "Cừu Thiên Đường")
        ),
        PetSpecies(
            id = "hedgehog",
            nameVi = "Nhím Xù",
            silhouette = SilhouetteType.HEDGEHOG,
            primaryColor = 0xFF94A3B8.toInt(),
            secondaryColor = 0xFFFFF7ED.toInt(),
            accentColor = 0xFF3B82F6.toInt(),
            stageNames = listOf("Trứng Gai Mềm", "Nhím Hạt Tiêu", "Nhím Lăn Tròn", "Nhím Siêu Thanh", "Nhím Tia Chớp")
        ),
        PetSpecies(
            id = "slime",
            nameVi = "Slime Sao",
            silhouette = SilhouetteType.SLIME,
            primaryColor = 0xFF2DD4BF.toInt(),
            secondaryColor = 0xFFCCFBF1.toInt(),
            accentColor = 0xFFFACC15.toInt(),
            stageNames = listOf("Trứng Thạch Xanh", "Slime Giọt Sương", "Slime Nảy Bật", "Slime Pha Lê", "Vua Slime Ngân Hà")
        )
    )

    fun findById(id: String): PetSpecies {
        return allSpecies.firstOrNull { it.id == id } ?: allSpecies.first()
    }
}
