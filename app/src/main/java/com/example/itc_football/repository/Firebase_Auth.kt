import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object Firebase_Auth {

    // 회원 정보를 저장할 데이터 클래스
    data class User(val name: String, val email: String, val dept: String)

    // Firebase 인증과 Firestore 인스턴스 생성
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // 유저 정보 저장 메서드
    fun saveUserInfo(name: String, email: String, dept: String) {
        // 현재 로그인한 유저 가져오기
        val user = auth.currentUser

        if (user != null) {
            // 사용자가 성공적으로 생성되었으면 Firestore에 데이터 추가
            val userData = User(name, email, dept)
            db.collection("users").document(user.uid)
                .set(userData)
                .addOnSuccessListener {
                    Log.d("Firebase", "DocumentSnapshot added with ID: ${user.uid}")
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error adding document", e)
                }
        } else {
            Log.w("Firebase", "No authenticated user")
        }
    }
}
