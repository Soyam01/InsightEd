// Main JavaScript file for InsightEd Platform
document.addEventListener('DOMContentLoaded', function() {
    console.log('InsightEd Platform loaded successfully!');

    // Initialize page-specific functionality
    initializeNavigation();
    initializeFormValidation();
    initializeDashboard();
    initializeDocumentViewer();
    initializeFileUpload();
    initializeNotifications();
    initializeAssignmentManagement();
});

// Assignment Management functionality
function initializeAssignmentManagement() {
    const createAssignmentBtn = document.getElementById('createAssignmentBtn');
    const assignmentForm = document.getElementById('assignmentForm');
    const cancelAssignmentBtn = document.getElementById('cancelAssignmentBtn');
    const newAssignmentForm = document.getElementById('newAssignmentForm');

    if (createAssignmentBtn && assignmentForm) {
        createAssignmentBtn.addEventListener('click', function() {
            assignmentForm.style.display = 'block';
            assignmentForm.scrollIntoView({ behavior: 'smooth' });
        });
    }

    if (cancelAssignmentBtn && assignmentForm) {
        cancelAssignmentBtn.addEventListener('click', function() {
            assignmentForm.style.display = 'none';
            if (newAssignmentForm) {
                newAssignmentForm.reset();
            }
        });
    }

    if (newAssignmentForm) {
        newAssignmentForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            const assignmentData = {
                title: formData.get('assignmentTitle'),
                course: formData.get('assignmentCourse'),
                type: formData.get('assignmentType'),
                points: formData.get('assignmentPoints'),
                dueDate: formData.get('assignmentDueDate'),
                description: formData.get('assignmentDescription'),
                submissionFormat: formData.get('submissionFormat'),
                category: formData.get('assignmentCategory'),
                allowLateSubmission: formData.get('allowLateSubmission') === 'on',
                sendNotification: formData.get('sendNotification') === 'on',
                enablePeerReview: formData.get('enablePeerReview') === 'on'
            };

            // Validate required fields
            if (!assignmentData.title || !assignmentData.course || !assignmentData.type ||
                !assignmentData.points || !assignmentData.dueDate || !assignmentData.description) {
                showNotification('Please fill in all required fields', 'error');
                return;
            }

            // Simulate assignment creation
            showNotification('Assignment created successfully!', 'success');

            // Hide form and reset
            assignmentForm.style.display = 'none';
            this.reset();

            // Optionally refresh the assignment list
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        });
    }

    // Filter functionality
    const courseFilter = document.getElementById('assignmentFilterCourse');
    const statusFilter = document.getElementById('assignmentFilterStatus');

    if (courseFilter) {
        courseFilter.addEventListener('change', function() {
            filterAssignments();
        });
    }

    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            filterAssignments();
        });
    }
}

function filterAssignments() {
    const courseFilter = document.getElementById('assignmentFilterCourse');
    const statusFilter = document.getElementById('assignmentFilterStatus');
    const rows = document.querySelectorAll('.table tbody tr');

    const selectedCourse = courseFilter ? courseFilter.value : '';
    const selectedStatus = statusFilter ? statusFilter.value : '';

    rows.forEach(row => {
        const courseCell = row.cells[1].textContent;
        const statusCell = row.cells[5].textContent.toLowerCase();

        const courseMatch = !selectedCourse || courseCell.includes(selectedCourse);
        const statusMatch = !selectedStatus || statusCell.includes(selectedStatus.toLowerCase());

        if (courseMatch && statusMatch) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

// Navigation functionality
function initializeNavigation() {
    // Sidebar navigation for dashboards
    const sidebarLinks = document.querySelectorAll('.sidebar-nav a[data-section]');
    const sections = document.querySelectorAll('.dashboard-section');

    sidebarLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            // Remove active class from all links
            sidebarLinks.forEach(l => l.classList.remove('active'));
            // Add active class to clicked link
            this.classList.add('active');

            // Hide all sections
            sections.forEach(section => section.classList.add('hidden'));

            // Show target section
            const targetSection = document.getElementById(this.dataset.section + '-section');
            if (targetSection) {
                targetSection.classList.remove('hidden');
            }
        });
    });

    // Mobile menu toggle
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const sidebar = document.getElementById('sidebar');

    if (mobileMenuBtn && sidebar) {
        mobileMenuBtn.addEventListener('click', function() {
            sidebar.classList.toggle('open');
        });
    }

    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

// Form validation
function initializeFormValidation() {
    // Login form validation
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const role = document.getElementById('role').value;

            if (!email || !password || !role) {
                showNotification('Please fill in all fields', 'error');
                return;
            }

            if (!isValidEmail(email)) {
                showNotification('Please enter a valid email address', 'error');
                return;
            }

            // Simulate login
            showNotification('Login successful! Redirecting...', 'success');
            setTimeout(() => {
                if (role === 'student') {
                    window.location.href = 'student-dashboard.html';
                } else {
                    window.location.href = 'teacher-dashboard.html';
                }
            }, 1500);
        });
    }

    // Signup form validation
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const firstName = document.getElementById('firstName').value;
            const lastName = document.getElementById('lastName').value;
            const email = document.getElementById('signupEmail').value;
            const password = document.getElementById('signupPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const role = document.getElementById('signupRole').value;

            if (!firstName || !lastName || !email || !password || !confirmPassword || !role) {
                showNotification('Please fill in all fields', 'error');
                return;
            }

            if (!isValidEmail(email)) {
                showNotification('Please enter a valid email address', 'error');
                return;
            }

            if (password.length < 6) {
                showNotification('Password must be at least 6 characters long', 'error');
                return;
            }

            if (password !== confirmPassword) {
                showNotification('Passwords do not match', 'error');
                return;
            }

            // Simulate signup
            showNotification('Account created successfully! Redirecting to login...', 'success');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 1500);
        });
    }

    // Upload form validation
    const uploadForm = document.getElementById('uploadForm');
    if (uploadForm) {
        uploadForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const title = document.getElementById('assignmentTitle').value;
            const course = document.getElementById('course').value;
            const file = document.getElementById('assignmentFile').files[0];

            if (!title || !course || !file) {
                showNotification('Please fill in all required fields and select a file', 'error');
                return;
            }

            // Simulate upload
            showNotification('Assignment uploaded successfully!', 'success');
            uploadForm.reset();
            document.getElementById('fileInfo').classList.add('hidden');
        });
    }
}

// Dashboard functionality
function initializeDashboard() {
    // Search functionality for teacher dashboard
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const tableRows = document.querySelectorAll('#submissionsTable tr');

            tableRows.forEach(row => {
                const studentName = row.cells[0]?.textContent.toLowerCase() || '';
                const assignment = row.cells[1]?.textContent.toLowerCase() || '';

                if (studentName.includes(searchTerm) || assignment.includes(searchTerm)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });
    }

    // Course filter functionality
    const courseFilter = document.getElementById('courseFilter');
    if (courseFilter) {
        courseFilter.addEventListener('change', function() {
            const selectedCourse = this.value;
            const tableRows = document.querySelectorAll('#submissionsTable tr');

            tableRows.forEach(row => {
                const course = row.cells[2]?.textContent || '';

                if (!selectedCourse || course === selectedCourse) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });
    }

    // Grade saving functionality
    document.querySelectorAll('.btn-success').forEach(btn => {
        if (btn.textContent.includes('Save Grade')) {
            btn.addEventListener('click', function() {
                const gradeInput = this.parentElement.parentElement.querySelector('input[type="number"]');
                const grade = gradeInput?.value;

                if (!grade || grade < 0 || grade > 100) {
                    showNotification('Please enter a valid grade (0-100)', 'error');
                    return;
                }

                showNotification('Grade saved successfully!', 'success');
                gradeInput.disabled = true;
                this.disabled = true;

                // Update status badge
                const statusBadge = this.parentElement.parentElement.parentElement.querySelector('.badge-pending');
                if (statusBadge) {
                    statusBadge.textContent = 'Reviewed';
                    statusBadge.className = 'badge badge-reviewed';
                }
            });
        }
    });
}

// Document viewer functionality
function initializeDocumentViewer() {
    const clickableTexts = document.querySelectorAll('.clickable-text');
    const commentForm = document.getElementById('commentForm');
    const selectedTextSpan = document.getElementById('selectedText');
    const commentTextArea = document.getElementById('commentText');
    const saveCommentBtn = document.getElementById('saveComment');
    const cancelCommentBtn = document.getElementById('cancelComment');
    const commentsList = document.getElementById('commentsList');

    let selectedParagraph = null;

    // Add hover effects to clickable text
    clickableTexts.forEach(text => {
        text.addEventListener('mouseenter', function() {
            this.style.backgroundColor = 'var(--very-light-blue)';
        });

        text.addEventListener('mouseleave', function() {
            if (this !== selectedParagraph) {
                this.style.backgroundColor = 'transparent';
            }
        });

        text.addEventListener('click', function() {
            // Remove selection from previous paragraph
            if (selectedParagraph) {
                selectedParagraph.style.backgroundColor = 'transparent';
            }

            // Select current paragraph
            selectedParagraph = this;
            this.style.backgroundColor = 'var(--light-blue)';

            // Show comment form
            if (commentForm) {
                commentForm.classList.remove('hidden');
                selectedTextSpan.textContent = `Selected: ${this.textContent.substring(0, 100)}...`;
                commentTextArea.focus();
            }
        });
    });

    // Save comment functionality
    if (saveCommentBtn) {
        saveCommentBtn.addEventListener('click', function() {
            const commentText = commentTextArea.value.trim();

            if (!commentText) {
                showNotification('Please enter a comment', 'error');
                return;
            }

            if (!selectedParagraph) {
                showNotification('Please select a text section first', 'error');
                return;
            }

            // Create new comment element
            const newComment = document.createElement('div');
            newComment.className = 'comment-item';
            newComment.innerHTML = `
                <div class="comment-author">Prof. Sarah Johnson</div>
                <div class="comment-text">${commentText}</div>
                <div class="comment-time">Just now</div>
                <div style="margin-top: 0.5rem;">
                    <span style="font-size: 0.75rem; color: var(--primary-blue); background: var(--light-blue); padding: 0.125rem 0.5rem; border-radius: 12px;">${selectedParagraph.id}</span>
                </div>
            `;

            // Add to comments list
            if (commentsList) {
                commentsList.insertBefore(newComment, commentsList.firstChild);
            }

            // Reset form
            commentTextArea.value = '';
            commentForm.classList.add('hidden');
            selectedParagraph.style.backgroundColor = 'transparent';
            selectedParagraph = null;

            showNotification('Comment added successfully!', 'success');
        });
    }

    // Cancel comment functionality
    if (cancelCommentBtn) {
        cancelCommentBtn.addEventListener('click', function() {
            commentTextArea.value = '';
            commentForm.classList.add('hidden');
            if (selectedParagraph) {
                selectedParagraph.style.backgroundColor = 'transparent';
                selectedParagraph = null;
            }
        });
    }

    // Highlight paragraphs when clicking on existing comments
    document.querySelectorAll('.comment-item').forEach(comment => {
        comment.addEventListener('click', function() {
            const targetId = this.dataset.target;
            const targetParagraph = document.getElementById(targetId);

            if (targetParagraph) {
                // Remove previous highlights
                clickableTexts.forEach(text => {
                    text.style.backgroundColor = 'transparent';
                });

                // Highlight target paragraph
                targetParagraph.style.backgroundColor = 'var(--light-blue)';
                targetParagraph.scrollIntoView({ behavior: 'smooth', block: 'center' });

                // Remove highlight after 3 seconds
                setTimeout(() => {
                    targetParagraph.style.backgroundColor = 'transparent';
                }, 3000);
            }
        });
    });

    // Grade saving in document viewer
    const saveGradeBtn = document.getElementById('saveGrade');
    if (saveGradeBtn) {
        saveGradeBtn.addEventListener('click', function() {
            const gradeInput = document.getElementById('gradeInput');
            const grade = gradeInput?.value;

            if (!grade || grade < 0 || grade > 100) {
                showNotification('Please enter a valid grade (0-100)', 'error');
                return;
            }

            showNotification(`Grade of ${grade}% saved successfully!`, 'success');
            gradeInput.disabled = true;
            this.disabled = true;
        });
    }
}

// File upload functionality
function initializeFileUpload() {
    const fileDropZone = document.getElementById('fileDropZone');
    const fileInput = document.getElementById('assignmentFile');
    const fileInfo = document.getElementById('fileInfo');
    const fileName = document.getElementById('fileName');
    const removeFileBtn = document.getElementById('removeFile');

    if (fileDropZone && fileInput) {
        // Click to browse
        fileDropZone.addEventListener('click', function() {
            fileInput.click();
        });

        // Drag and drop functionality
        fileDropZone.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.style.borderColor = 'var(--primary-blue)';
            this.style.backgroundColor = 'var(--very-light-blue)';
        });

        fileDropZone.addEventListener('dragleave', function(e) {
            e.preventDefault();
            this.style.borderColor = 'var(--gray-300)';
            this.style.backgroundColor = 'var(--gray-50)';
        });

        fileDropZone.addEventListener('drop', function(e) {
            e.preventDefault();
            this.style.borderColor = 'var(--gray-300)';
            this.style.backgroundColor = 'var(--gray-50)';

            const files = e.dataTransfer.files;
            if (files.length > 0) {
                handleFileSelection(files[0]);
            }
        });

        // File input change
        fileInput.addEventListener('change', function() {
            if (this.files.length > 0) {
                handleFileSelection(this.files[0]);
            }
        });

        // Remove file functionality
        if (removeFileBtn) {
            removeFileBtn.addEventListener('click', function() {
                fileInput.value = '';
                fileInfo.classList.add('hidden');
            });
        }
    }

    function handleFileSelection(file) {
        const maxSize = 10 * 1024 * 1024; // 10MB
        const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'text/plain'];

        if (file.size > maxSize) {
            showNotification('File size must be less than 10MB', 'error');
            return;
        }

        if (!allowedTypes.includes(file.type)) {
            showNotification('Please select a valid file type (PDF, DOC, DOCX, TXT)', 'error');
            return;
        }

        if (fileName && fileInfo) {
            fileName.textContent = `📄 ${file.name} (${formatFileSize(file.size)})`;
            fileInfo.classList.remove('hidden');
        }
    }

    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
}

// Notifications functionality
function initializeNotifications() {
    const notificationBtn = document.getElementById('notificationBtn');
    const notificationDropdown = document.getElementById('notificationDropdown');

    if (notificationBtn && notificationDropdown) {
        notificationBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            notificationDropdown.classList.toggle('hidden');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function() {
            notificationDropdown.classList.add('hidden');
        });

        notificationDropdown.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    }
}

// Utility functions
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());

    // Create notification element
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        border-radius: var(--border-radius);
        color: white;
        font-weight: 500;
        z-index: 1000;
        animation: slideIn 0.3s ease-out;
        max-width: 400px;
        box-shadow: var(--shadow-lg);
    `;

    // Set background color based on type
    switch (type) {
        case 'success':
            notification.style.backgroundColor = 'var(--success)';
            break;
        case 'error':
            notification.style.backgroundColor = 'var(--error)';
            break;
        case 'warning':
            notification.style.backgroundColor = 'var(--warning)';
            break;
        default:
            notification.style.backgroundColor = 'var(--primary-blue)';
    }

    notification.textContent = message;
    document.body.appendChild(notification);

    // Auto remove after 4 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 4000);
}

// Add slideOut animation
const style = document.createElement('style');
style.textContent = `
    @keyframes slideOut {
        from {
            opacity: 1;
            transform: translateX(0);
        }
        to {
            opacity: 0;
            transform: translateX(100%);
        }
    }
`;
document.head.appendChild(style);

// Add some interactive animations on page load
function addPageAnimations() {
    // Animate feature cards on scroll
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animation = 'fadeInUp 0.6s ease-out';
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Observe feature cards
    document.querySelectorAll('.feature-card, .card').forEach(card => {
        observer.observe(card);
    });
}

// Initialize animations when DOM is loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', addPageAnimations);
} else {
    addPageAnimations();
}