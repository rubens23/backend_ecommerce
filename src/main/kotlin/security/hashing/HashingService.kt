package security.hashing

interface HashingService {
    fun generateSaltedHash(value: String, saltedLength: Int = 32, iterations: Int = 10000): SaltedHash
    fun verify(value: String, saltedHash: SaltedHash): Boolean
}