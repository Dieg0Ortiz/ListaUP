package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.AttendanceStatus
import com.example.myapplication.domain.model.StudentAttendance
import com.example.myapplication.domain.usecase.GetStudentsOfGroupUseCase
import com.example.myapplication.domain.usecase.SubmitAttendanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AttendanceUiState(
    val isLoading: Boolean = false,
    val students: List<StudentAttendance> = emptyList(),
    val searchQuery: String = "",
    val selectedGroup: String = "",
    val registeredCount: Int = 0,
    val totalCount: Int = 0,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null
)

class AttendanceViewModel(
    private val getStudentsOfGroupUseCase: GetStudentsOfGroupUseCase,
    private val submitAttendanceUseCase: SubmitAttendanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    fun loadStudents(grupoId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, selectedGroup = grupoId)
        viewModelScope.launch {
            val result = getStudentsOfGroupUseCase(grupoId)
            result.fold(
                onSuccess = { students ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        students = students,
                        totalCount = students.size,
                        registeredCount = 0
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun updateStudentStatus(studentId: String, status: AttendanceStatus) {
        val updated = _uiState.value.students.map { student ->
            if (student.studentId == studentId) student.copy(status = status)
            else student
        }
        val registered = updated.count { it.status != AttendanceStatus.AUSENTE }
        _uiState.value = _uiState.value.copy(
            students = updated,
            registeredCount = registered
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun markAllPresent() {
        val updated = _uiState.value.students.map { student ->
            if (student.hasPermission) student else student.copy(status = AttendanceStatus.PRESENTE)
        }
        _uiState.value = _uiState.value.copy(
            students = updated,
            registeredCount = updated.count { it.status != AttendanceStatus.AUSENTE }
        )
    }

    fun getFilteredStudents(): List<StudentAttendance> {
        val query = _uiState.value.searchQuery.lowercase()
        if (query.isBlank()) return _uiState.value.students
        return _uiState.value.students.filter {
            it.studentName.lowercase().contains(query) ||
                    it.matricula.lowercase().contains(query)
        }
    }

    fun submitAttendance() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            val result = submitAttendanceUseCase(
                _uiState.value.selectedGroup,
                _uiState.value.students
            )
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        isSubmitted = true
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = e.message
                    )
                }
            )
        }
    }
}
