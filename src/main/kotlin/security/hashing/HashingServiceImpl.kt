package security.hashing

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class HashingServiceImpl: HashingService {
    override fun generateSaltedHash(value: String, saltedLength: Int, iterations: Int): SaltedHash {
        // Gerar o salt aleatório
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltedLength)

        // Definir o número de iterações para o pbkdf2
        val keySpec = PBEKeySpec(value.toCharArray(), salt, iterations, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")

        //gerar o hash
        val hashBytes = secretKeyFactory.generateSecret(keySpec).encoded

        // Codificar o hash e salt em Base64 para armazenamento
        val hashBase64 = Base64.getEncoder().encodeToString(hashBytes)
        val saltBase64 = Base64.getEncoder().encodeToString(salt)

        return SaltedHash(
            hash = hashBase64,
            salt = saltBase64
        )



    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        // Recuperar o salt e hash armazenado
        val salt = Base64.getDecoder().decode(saltedHash.salt)
        val storedHash = Base64.getDecoder().decode(saltedHash.hash)

        // Regerar o hash com o valor fornecido e o salt armazenado
        val keySpec = PBEKeySpec(value.toCharArray(), salt, 10000, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val generatedHashBytes = secretKeyFactory.generateSecret(keySpec).encoded

        // Comparar o hash gerado com o hash armazenado
        return storedHash.contentEquals(generatedHashBytes)

    }
}