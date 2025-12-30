package smart.planner.data.local

import smart.planner.data.model.QuoteResponse

/**
 * Danh sách các câu trích dẫn động viên học tập bằng Tiếng Việt
 * Sử dụng local data thay vì API để đảm bảo có quotes tiếng Việt phù hợp
 */
object VietnameseQuotes {
    
    /**
     * Danh sách quotes tiếng Việt về học tập và động lực
     */
    private val quotes = listOf(
        QuoteResponse(
            id = "vn_001",
            content = "Học tập là hành trình suốt đời, không phải là đích đến.",
            author = "Khuyết danh",
            tags = listOf("học tập", "động lực"),
            authorSlug = null,
            length = 45,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_002",
            content = "Thành công không đến với những người chỉ biết chờ đợi. Nó đến với những người biết hành động.",
            author = "Khuyết danh",
            tags = listOf("thành công", "hành động"),
            authorSlug = null,
            length = 78,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_003",
            content = "Đừng sợ thất bại. Hãy sợ việc không dám thử.",
            author = "Khuyết danh",
            tags = listOf("thất bại", "dũng cảm"),
            authorSlug = null,
            length = 38,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_004",
            content = "Kiến thức là sức mạnh. Học tập là quyền lực.",
            author = "Francis Bacon",
            tags = listOf("kiến thức", "học tập"),
            authorSlug = null,
            length = 40,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_005",
            content = "Hôm nay bạn đã học được gì? Mỗi ngày là một cơ hội mới để phát triển.",
            author = "Khuyết danh",
            tags = listOf("học tập", "phát triển"),
            authorSlug = null,
            length = 65,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_006",
            content = "Thành công là tổng của những nỗ lực nhỏ được lặp lại hàng ngày.",
            author = "Robert Collier",
            tags = listOf("thành công", "nỗ lực"),
            authorSlug = null,
            length = 58,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_007",
            content = "Đừng so sánh bản thân với người khác. Hãy so sánh với chính bạn ngày hôm qua.",
            author = "Khuyết danh",
            tags = listOf("tự phát triển", "động lực"),
            authorSlug = null,
            length = 72,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_008",
            content = "Học tập không bao giờ là lãng phí thời gian. Mỗi kiến thức mới là một kho báu.",
            author = "Khuyết danh",
            tags = listOf("học tập", "kiến thức"),
            authorSlug = null,
            length = 70,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_009",
            content = "Thất bại chỉ là cơ hội để bắt đầu lại một cách thông minh hơn.",
            author = "Henry Ford",
            tags = listOf("thất bại", "học hỏi"),
            authorSlug = null,
            length = 55,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_010",
            content = "Bạn không cần phải giỏi để bắt đầu, nhưng bạn cần bắt đầu để trở nên giỏi.",
            author = "Zig Ziglar",
            tags = listOf("bắt đầu", "động lực"),
            authorSlug = null,
            length = 68,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_011",
            content = "Học tập là khoản đầu tư tốt nhất cho tương lai của bạn.",
            author = "Khuyết danh",
            tags = listOf("học tập", "tương lai"),
            authorSlug = null,
            length = 48,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_012",
            content = "Mỗi chuyên gia đều từng là người mới bắt đầu. Mỗi người chuyên nghiệp đều từng là nghiệp dư.",
            author = "Helen Hayes",
            tags = listOf("phát triển", "kiên trì"),
            authorSlug = null,
            length = 75,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_013",
            content = "Động lực giúp bạn bắt đầu. Thói quen giúp bạn tiếp tục.",
            author = "Jim Ryun",
            tags = listOf("động lực", "thói quen"),
            authorSlug = null,
            length = 48,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_014",
            content = "Học tập là chìa khóa mở ra mọi cánh cửa của tương lai.",
            author = "Khuyết danh",
            tags = listOf("học tập", "tương lai"),
            authorSlug = null,
            length = 50,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_015",
            content = "Thời gian bạn đầu tư vào học tập sẽ không bao giờ là lãng phí.",
            author = "Khuyết danh",
            tags = listOf("học tập", "thời gian"),
            authorSlug = null,
            length = 56,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_016",
            content = "Đừng dừng lại khi bạn mệt mỏi. Dừng lại khi bạn đã hoàn thành.",
            author = "Khuyết danh",
            tags = listOf("kiên trì", "hoàn thành"),
            authorSlug = null,
            length = 56,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_017",
            content = "Kiến thức không có trọng lượng. Bạn có thể mang theo nó đi khắp nơi.",
            author = "Khuyết danh",
            tags = listOf("kiến thức", "học tập"),
            authorSlug = null,
            length = 60,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_018",
            content = "Học tập từ thất bại là cách tốt nhất để tránh lặp lại sai lầm.",
            author = "Khuyết danh",
            tags = listOf("thất bại", "học hỏi"),
            authorSlug = null,
            length = 58,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_019",
            content = "Mục tiêu không phải là đọc nhiều sách, mà là hiểu sâu những gì bạn đọc.",
            author = "Khuyết danh",
            tags = listOf("học tập", "hiểu biết"),
            authorSlug = null,
            length = 66,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_020",
            content = "Hôm nay là ngày tốt nhất để bắt đầu học điều gì đó mới.",
            author = "Khuyết danh",
            tags = listOf("bắt đầu", "học tập"),
            authorSlug = null,
            length = 50,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_021",
            content = "Thành công không phải là đích đến cuối cùng, thất bại cũng không phải là chết chóc. Điều quan trọng là có can đảm để tiếp tục.",
            author = "Winston Churchill",
            tags = listOf("thành công", "kiên trì"),
            authorSlug = null,
            length = 95,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_022",
            content = "Học tập là hành trình, không phải là đích đến. Hãy tận hưởng từng bước đi.",
            author = "Khuyết danh",
            tags = listOf("học tập", "hành trình"),
            authorSlug = null,
            length = 66,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_023",
            content = "Bạn không thể thay đổi ngày hôm qua, nhưng bạn có thể bắt đầu ngay hôm nay để tạo ra ngày mai tốt đẹp hơn.",
            author = "Khuyết danh",
            tags = listOf("tương lai", "hành động"),
            authorSlug = null,
            length = 95,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_024",
            content = "Mỗi bài học là một bước tiến. Mỗi thử thách là một cơ hội.",
            author = "Khuyết danh",
            tags = listOf("học tập", "thử thách"),
            authorSlug = null,
            length = 50,
            dateAdded = null,
            dateModified = null
        ),
        QuoteResponse(
            id = "vn_025",
            content = "Đừng sợ tiến bộ chậm. Hãy sợ việc đứng yên.",
            author = "Khuyết danh",
            tags = listOf("tiến bộ", "động lực"),
            authorSlug = null,
            length = 42,
            dateAdded = null,
            dateModified = null
        )
    )
    
    /**
     * Lấy một quote ngẫu nhiên từ danh sách
     */
    fun getRandomQuote(): QuoteResponse {
        return quotes.random()
    }
    
    /**
     * Lấy tất cả quotes
     */
    fun getAllQuotes(): List<QuoteResponse> {
        return quotes
    }
    
    /**
     * Lấy số lượng quotes
     */
    fun getQuoteCount(): Int {
        return quotes.size
    }
}

