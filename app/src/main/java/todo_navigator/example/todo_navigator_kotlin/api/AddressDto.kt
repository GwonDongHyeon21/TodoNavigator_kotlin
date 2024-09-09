package todo_navigator.example.todo_navigator_kotlin.api

data class AddressResponse(
    val addresses: List<AddressItem>
) {
    data class AddressItem(
        val roadAddress: String,
        val jibunAddress: String,
        val x: Double,
        val y: Double,
    )
}
