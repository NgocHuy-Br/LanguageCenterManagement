const API_BASE = 'http://localhost:8080';

function encodeBasicCredentials(username, password) {
    const bytes = new TextEncoder().encode(username + ':' + password);
    let binary = '';
    for (const byte of bytes) {
        binary += String.fromCharCode(byte);
    }
    return btoa(binary);
}

function saveAuth(username, password, role, profile) {
    const token = encodeBasicCredentials(username, password);
    sessionStorage.setItem('auth_token', token);
    sessionStorage.setItem('auth_user', username);
    sessionStorage.setItem('auth_role', role);
    if (profile) {
        sessionStorage.setItem('auth_profile', JSON.stringify(profile));
    }
}

function getToken() {
    return sessionStorage.getItem('auth_token');
}

function getUser() {
    return sessionStorage.getItem('auth_user') || '';
}

function getRole() {
    return sessionStorage.getItem('auth_role') || '';
}

function getProfile() {
    const raw = sessionStorage.getItem('auth_profile');
    return raw ? JSON.parse(raw) : null;
}

function clearAuth() {
    sessionStorage.clear();
}

function requireAuth(expectedRole) {
    const token = getToken();
    const role = getRole();
    if (!token) {
        window.location.href = '../index.html';
        return false;
    }
    if (expectedRole && role !== expectedRole) {
        alert('Bạn không có quyền truy cập trang này.');
        window.location.href = '../index.html';
        return false;
    }
    return true;
}

async function apiFetch(method, path, body) {
    const token = getToken();
    const headers = {};
    if (token) headers['Authorization'] = 'Basic ' + token;
    if (body !== undefined) headers['Content-Type'] = 'application/json';

    const opts = { method, headers };
    if (body !== undefined) opts.body = JSON.stringify(body);

    const res = await fetch(API_BASE + path, opts);
    if (res.status === 401) {
        clearAuth();
        window.location.href = '../index.html';
        return null;
    }
    return res;
}

async function login(username, password) {
    const token = encodeBasicCredentials(username, password);
    const res = await fetch(API_BASE + '/api/me', {
        method: 'GET',
        headers: { 'Authorization': 'Basic ' + token }
    });

    if (!res.ok) {
        return null;
    }

    const payload = await res.json();
    const profile = payload.data || {};
    return {
        role: profile.role,
        token,
        profile
    };
}

// ===== STUDENT API =====

async function getStudents() {
    const res = await apiFetch('GET', '/api/admin/students');
    if (!res || !res.ok) return [];
    const data = await res.json();
    return data.data || [];
}

async function createStudent(student) {
    const res = await apiFetch('POST', '/api/admin/students', student);
    if (!res) return null;
    const data = await res.json();
    return { ok: res.ok, data: data.data, message: data.message };
}

async function updateStudent(id, student) {
    const res = await apiFetch('PUT', '/api/admin/students/' + id, student);
    if (!res) return null;
    const data = await res.json();
    return { ok: res.ok, data: data.data, message: data.message };
}

async function deleteStudent(id) {
    const res = await apiFetch('DELETE', '/api/admin/students/' + id);
    if (!res) return false;
    return res.ok;
}

// ===== TEACHER API =====

async function getTeachers() {
    const res = await apiFetch('GET', '/api/admin/teachers');
    if (!res || !res.ok) return [];
    const data = await res.json();
    return data.data || [];
}

async function createTeacher(teacher) {
    const res = await apiFetch('POST', '/api/admin/teachers', teacher);
    if (!res) return null;
    const data = await res.json();
    return { ok: res.ok, data: data.data, message: data.message };
}

async function updateTeacher(id, teacher) {
    const res = await apiFetch('PUT', '/api/admin/teachers/' + id, teacher);
    if (!res) return null;
    const data = await res.json();
    return { ok: res.ok, data: data.data, message: data.message };
}

async function deleteTeacher(id) {
    const res = await apiFetch('DELETE', '/api/admin/teachers/' + id);
    if (!res) return false;
    return res.ok;
}

// ===== CLASSROOM API =====

async function getClassrooms() {
    const res = await apiFetch('GET', '/api/admin/classes');
    if (!res || !res.ok) return [];
    const data = await res.json();
    return data.data || [];
}

async function createClassroom(classroom, teacherId) {
    const query = teacherId ? ('?teacherId=' + encodeURIComponent(teacherId)) : '';
    const res = await apiFetch('POST', '/api/admin/classes' + query, classroom);
    if (!res) return null;
    const data = await res.json();
    return { ok: res.ok, data: data.data, message: data.message };
}

async function updateClassroom(id, classroom, teacherId) {
    const query = teacherId ? ('?teacherId=' + encodeURIComponent(teacherId)) : '';
    const res = await apiFetch('PUT', '/api/admin/classes/' + id + query, classroom);
    if (!res) return null;
    const data = await res.json();
    return { ok: res.ok, data: data.data, message: data.message };
}

async function deleteClassroom(id) {
    const res = await apiFetch('DELETE', '/api/admin/classes/' + id);
    if (!res) return false;
    return res.ok;
}

// ===== CLASS-STUDENT API =====

async function getClassStudents() {
    const res = await apiFetch('GET', '/api/admin/class-students');
    if (!res || !res.ok) return [];
    const data = await res.json();
    return data.data || [];
}

async function addStudentToClass(classroomId, studentId) {
    const res = await apiFetch('POST', '/api/admin/class-students/add?classroomId=' + classroomId + '&studentId=' + studentId);
    if (!res) return null;
    const data = await res.json();
    return { ok: res.ok, data: data.data, message: data.message };
}

async function addStudentsToClass(classroomId, studentIds) {
    const results = [];
    for (const studentId of studentIds) {
        results.push(await addStudentToClass(classroomId, studentId));
    }
    return results;
}

async function removeStudentFromClass(id) {
    const res = await apiFetch('DELETE', '/api/admin/class-students/' + id);
    if (!res) return false;
    return res.ok;
}

// ===== RESULT API (admin view) =====

async function getAllResults() {
    const res = await apiFetch('GET', '/api/admin/results');
    if (!res || !res.ok) return [];
    const data = await res.json();
    return data.data || [];
}

async function getStudentResults(studentId) {
    const res = await apiFetch('GET', '/api/student/results/' + studentId);
    if (!res || !res.ok) return [];
    const data = await res.json();
    return data.data || [];
}

// ===== TEACHER - VIEW CLASSES & STUDENTS =====

async function getTeacherClasses() {
    const res = await apiFetch('GET', '/api/teacher/classes');
    if (!res || !res.ok) return [];
    const data = await res.json();
    return data.data || [];
}

async function getTeacherClassStudents(classroomId) {
    const res = await apiFetch('GET', '/api/teacher/class-students?classroomId=' + classroomId);
    if (!res || !res.ok) return [];
    const data = await res.json();
    return data.data || [];
}

async function getTeacherResultsByClass(classroomId) {
    const res = await apiFetch('GET', '/api/teacher/results?classroomId=' + classroomId);
    if (!res || !res.ok) return [];
    const data = await res.json();
    return data.data || [];
}

// ===== RESULT API (teacher) =====

async function createResult(classStudentId, result) {
    const res = await apiFetch('POST', '/api/teacher/results?classStudentId=' + classStudentId, result);
    if (!res) return null;
    const data = await res.json();
    return { ok: res.ok, data: data.data, message: data.message };
}

async function updateResult(id, result) {
    const res = await apiFetch('PUT', '/api/teacher/results/' + id, result);
    if (!res) return null;
    const data = await res.json();
    return { ok: res.ok, data: data.data, message: data.message };
}

async function deleteResult(id) {
    const res = await apiFetch('DELETE', '/api/teacher/results/' + id);
    if (!res) return false;
    return res.ok;
}

// ===== TOAST =====

function showToast(msg, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = 'toast ' + type;
    toast.textContent = msg;
    container.appendChild(toast);
    toast.addEventListener('click', () => toast.remove());
    setTimeout(() => { if (toast.parentNode) toast.remove(); }, 3500);
}
