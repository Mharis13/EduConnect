using System.Security.Claims;
using EduconnectAPI.Config;
using EduconnectAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using takDto = EduconnectAPI.Models.Task;

namespace EduconnectAPI.Controllers
{
    /// <summary>
    /// Controller for managing courses and related operations.
    /// </summary>
    [ApiController]
    [Authorize]
    [Route("api/v1/course/")]
    public class CourseController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        /// <summary>
        /// Initializes a new instance of the <see cref="CourseController"/> class.
        /// </summary>
        /// <param name="context">The application database context.</param>
        public CourseController(ApplicationDbContext context)
        {
            _context = context;
        }

        /// <summary>
        /// Gets all courses.
        /// </summary>
        /// <returns>List of all courses.</returns>
        [HttpGet]
        public async Task<IActionResult> GetCourses()
        {
            var courses = await _context.Courses.ToListAsync();
            return Ok(courses);
        }

        /// <summary>
        /// Gets the courses in which a student is enrolled.
        /// </summary>
        /// <param name="studentId">The student identifier.</param>
        /// <returns>List of courses for the student.</returns>
        [HttpGet("student/{studentId}/courses")]
        [Authorize(Roles = "Student")]
        public async Task<IActionResult> GetCoursesByStudent(string studentId)
        {
            var courses = await _context.Enrollments
                .Where(e => e.UserId == studentId)
                .Select(e => e.Course)
                .ToListAsync();

            if (!courses.Any())
                return NotFound(new { message = "No courses found for this student." });

            return Ok(courses);
        }

        /// <summary>
        /// Gets a course by its identifier.
        /// </summary>
        /// <param name="id">The course identifier.</param>
        /// <returns>The course if found; otherwise, 404.</returns>
        [HttpGet("{id}")]
        [ProducesResponseType(statusCode: StatusCodes.Status200OK, type: typeof(Course))]
        [ProducesResponseType(statusCode: StatusCodes.Status404NotFound, type: typeof(NotFoundResult))]
        public async Task<IActionResult> GetCourseById(int id)
        {
            var course = await _context.Courses.FindAsync(id);
            if (course == null) return NotFound();
            return Ok(course);
        }

        /// <summary>
        /// Creates a new course. Only accessible by teachers.
        /// </summary>
        /// <param name="courseDto">The course data transfer object.</param>
        /// <returns>The created course.</returns>
        [HttpPost]
        [Authorize(Roles = "Teacher")]
        public async Task<IActionResult> CreateCourse([FromBody] CourseDto courseDto)
        {
            var creatorId = User.FindFirstValue(ClaimTypes.Email);
            if (string.IsNullOrEmpty(creatorId))
                return Unauthorized("Could not get the user id.");

            var course = new Course
            {
                Name = courseDto.Name,
                Description = courseDto.Description,
                CreatorId = creatorId,
                CreadedAt = DateTime.UtcNow
            };

            _context.Courses.Add(course);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetCourseById), new { id = course.Id }, course);
        }

        /// <summary>
        /// Deletes a course by its identifier. Only accessible by teachers.
        /// </summary>
        /// <param name="id">The course identifier.</param>
        /// <returns>No content if deleted; otherwise, 404.</returns>
        [HttpDelete("{id}")]
        [Authorize(Roles = "Teacher")]
        public async Task<IActionResult> DeleteCourseById(int id)
        {
            var course = await _context.Courses.FindAsync(id);
            if (course == null) return NotFound();
            _context.Courses.Remove(course);
            await _context.SaveChangesAsync();
            return NoContent();
        }

        /// <summary>
        /// Adds or updates a link for a task for the current user.
        /// </summary>
        /// <param name="taskId">The task identifier.</param>
        /// <param name="link">The link to add or update.</param>
        /// <returns>Success message.</returns>
        [HttpPost("task/{taskId}/link")]
        public async Task<IActionResult> AddOrUpdateLink(int taskId, [FromBody] string link)
        {
            var userId = User.FindFirstValue(ClaimTypes.Email);
            if (string.IsNullOrEmpty(userId))
                return Unauthorized("Could not get the user id.");

            var grade = await _context.Grades
                .FirstOrDefaultAsync(g => g.UserId == userId && g.TaskId == taskId);

            if (grade == null)
            {
                grade = new Grade
                {
                    UserId = userId,
                    TaskId = taskId,
                    Link = link
                };
                _context.Grades.Add(grade);
            }
            else
            {
                grade.Link = link;
            }

            await _context.SaveChangesAsync();
            return Ok(new { message = "Link saved successfully." });
        }

        /// <summary>
        /// Adds a task to a course. Only accessible by teachers.
        /// </summary>
        /// <param name="courseId">The course identifier.</param>
        /// <param name="taskDto">The task data transfer object.</param>
        /// <returns>The created task.</returns>
        [HttpPost("{courseId}/task")]
        [Authorize(Roles = "Teacher")]
        public async Task<IActionResult> AddTaskToCourse(int courseId, [FromBody] TaskDto taskDto)
        {
            var course = await _context.Courses.FindAsync(courseId);
            if (course == null) return NotFound(new { message = "Course not found" });

            var task = new takDto
            {
                Title = taskDto.Title,
                Description = taskDto.Description,
                DueDate = taskDto.DueDate,
                CourseId = courseId
            };

            _context.Tasks.Add(task);
            await _context.SaveChangesAsync();

            // Create a DTO for the response
            var responseDto = new TaskDto
            {
                Id = task.Id,
                Title = task.Title,
                Description = task.Description,
                DueDate = task.DueDate,
                CourseId = task.CourseId
            };

            return CreatedAtAction(nameof(TaskController.GetTaskById), "Task", new { id = task.Id }, responseDto);
        }

        /// <summary>
        /// Gets the students enrolled in a course.
        /// </summary>
        /// <param name="id">The course identifier.</param>
        /// <returns>List of students in the course.</returns>
        [HttpGet("{id}/students")]
        public async Task<IActionResult> GetStudentsInCourse(int id)
        {
            var course = await _context.Courses.FindAsync(id);
            if (course == null) return NotFound(new { message = "Course not found" });

            var students = await _context.Enrollments
                .Where(e => e.CourseId == id)
                .Select(e => e.User)
                .ToListAsync();

            return Ok(students);
        }

        /// <summary>
        /// Gets the courses in which a user is enrolled.
        /// </summary>
        /// <param name="userId">The user identifier.</param>
        /// <returns>List of courses for the user.</returns>
        [HttpGet("user/{userId}/courses")]
        public async Task<IActionResult> GetCoursesByUser(string userId)
        {
            var courses = await _context.Enrollments
                .Where(e => e.UserId.Equals(userId))
                .Select(e => e.Course)
                .ToListAsync();

            return Ok(courses);
        }

        /// <summary>
        /// Gets the courses created by a teacher. Only accessible by teachers.
        /// </summary>
        /// <param name="teacherId">The teacher identifier.</param>
        /// <returns>List of courses created by the teacher.</returns>
        [HttpGet("teacher/{teacherId}/courses")]
        [Authorize(Roles = "Teacher")]
        public async Task<IActionResult> GetCoursesCreatedByTeacher(string teacherId)
        {
            Console.WriteLine(teacherId);
            var courses = await _context.Courses
                .Where(c => c.CreatorId.Equals(teacherId))
                .ToListAsync();

            if (!courses.Any())
                return NotFound(new { message = "No courses found created by this teacher." });

            return Ok(courses);
        }

        /// <summary>
        /// Gets the students enrolled in a course (only students).
        /// </summary>
        /// <param name="id">The course identifier.</param>
        /// <returns>List of enrolled students.</returns>
        [HttpGet("{id}/students/enrolled")]
        public async Task<IActionResult> GetEnrolledStudentsInCourse(int id)
        {
            var course = await _context.Courses.FindAsync(id);
            if (course == null) return NotFound(new { message = "Course not found" });

            var students = await _context.Enrollments
                .Where(e => e.CourseId == id && e.User.Role == "Student")
                .Select(e => e.User)
                .ToListAsync();

            return Ok(students);
        }

        /// <summary>
        /// Adds students to a course. Only accessible by teachers.
        /// </summary>
        /// <param name="id">The course identifier.</param>
        /// <param name="userIds">List of user identifiers to add.</param>
        /// <returns>No content if successful; otherwise, error message.</returns>
        [HttpPost("{id}/students")]
        [Authorize(Roles = "Teacher")]
        public async Task<IActionResult> AddStudentsToCourse(int id, [FromBody] List<string> userIds)
        {
            var course = await _context.Courses.FindAsync(id);
            if (course == null) return NotFound(new { message = "Course not found" });

            // Get the IDs of students already enrolled in the course
            var existingStudentIds = await _context.Enrollments
                .Where(e => e.CourseId == id)
                .Select(e => e.UserId)
                .ToListAsync();

            // Filter the IDs of students who are already enrolled
            var newStudentIds = userIds.Except(existingStudentIds).ToList();

            if (!newStudentIds.Any())
            {
                return BadRequest(new { message = "All provided students are already enrolled in the course" });
            }

            // Create new enrollments only for students not already enrolled
            var enrollmentsToAdd = newStudentIds.Select(userId => new Enrollment
            {
                CourseId = id,
                UserId = userId
            });

            _context.Enrollments.AddRange(enrollmentsToAdd);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        /// <summary>
        /// Removes students from a course. Only accessible by teachers.
        /// </summary>
        /// <param name="id">The course identifier.</param>
        /// <param name="userIds">List of user identifiers to remove.</param>
        /// <returns>Success message.</returns>
        [HttpDelete("{id}/students")]
        [Authorize(Roles = "Teacher")]
        public async Task<IActionResult> DeleteStudentFromCourse(int id, [FromBody] List<string> userIds)
        {
            var enrollments = _context.Enrollments
                .Where(e => e.CourseId == id && userIds.Contains(e.UserId));

            _context.Enrollments.RemoveRange(enrollments);
            await _context.SaveChangesAsync();
            return Ok(new { message = "Students removed" });
        }
    }
}