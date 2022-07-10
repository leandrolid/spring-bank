package spring.leandrolid.bank.model

import org.hibernate.Hibernate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name = "accounts")
data class Account(
    @Id @GeneratedValue
    private val id: Long? = null,
    val name: String,
    val document: String,
    val phone: String,
) {
    fun getId() = id
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(
                other
            )
        ) return false
        other as Account

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , document = $document , phone = $phone )"
    }
}
