package site.jagged.planneriti.domain.model

data class StudentInfo(
    val name: String,
    val firstName: String,
    val patronymic: String,
    val studyYear: String,
    val yearNumber: Int?,
    val group: String,
    val specialization: String,
    val curator: String? = null,
    val status: String? = null
)

data class GradeSubject(
    val name: String,
    val grades: List<String>,
    val baseAverage: Double?,
    val finalAverage: Double?,
    val appliedExamType: String? = null,
    val appliedExamGrade: Double? = null
)

data class SemesterGrades(
    val semester: Int,
    val subjects: List<GradeSubject>,
    val totalAbsences: Int = 0,
    val unexcusedAbsences: Int = 0
)

data class Exam(
    val name: String,
    val type: String,
    val grade: String,
    val semester: Int,
    val isUpcoming: Boolean = false
)

data class StudentGrades(
    val studentInfo: StudentInfo,
    val semesters: List<SemesterGrades>,
    val exams: List<Exam>
)