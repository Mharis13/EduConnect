using EduconnectAPI.Config;
using EduconnectAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace EduconnectAPI.Controllers
{
    /// <summary>
    /// Controller responsible for managing user-related operations.
    /// </summary>
    [Route("api/v1/user")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        /// <summary>
        /// Initializes a new instance of the <see cref="UserController"/> class.
        /// </summary>
        /// <param name="context">The application database context.</param>
        public UserController(ApplicationDbContext context)
        {
            _context = context;
        }

        /// <summary>
        /// Retrieves all users in the system.
        /// </summary>
        /// <returns>A list of all users.</returns>
        [HttpGet]
        public async Task<IActionResult> GetAllUsers()
        {
            var users = await _context.Users.ToListAsync();
            return Ok(users);
        }

        /// <summary>
        /// Retrieves all students who are not enrolled in a specific course.
        /// Only accessible by users with the Teacher or Student role.
        /// </summary>
        /// <param name="courseId">The course identifier.</param>
        /// <returns>A list of students not enrolled in the course.</returns>
        [HttpGet("/students/course/{courseId}")]
        [Authorize(Roles = "Teacher,Student")]
        public async Task<IActionResult> GetAllStudentsThatAreNotinTheCourse(int courseId)
        {
            // Get the IDs of students already enrolled in the course
            var enrolledStudentIds = await _context.Enrollments
                .Where(e => e.CourseId == courseId)
                .Select(e => e.UserId)
                .ToListAsync();

            // Get all users with role "Student" who are NOT enrolled in the course
            var studentsNotInCourse = await _context.Users
                .Where(u => u.Role == "Student" && !enrolledStudentIds.Contains(u.Id))
                .ToListAsync();

            return Ok(studentsNotInCourse);
        }

        /// <summary>
        /// Retrieves all users with the Teacher role.
        /// </summary>
        /// <returns>A list of teachers.</returns>
        [HttpGet("teachers")]
        public async Task<IActionResult> GetAllTeachers()
        {
            var teachers = await _context.Users
                .Where(u => u.Role == "Teacher")
                .ToListAsync();

            return Ok(teachers);
        }

        /// <summary>
        /// Retrieves a user by their identifier.
        /// </summary>
        /// <param name="id">The user identifier.</param>
        /// <returns>The user if found, otherwise 404.</returns>
        [HttpGet("{id}")]
        public async Task<IActionResult> GetUserById(string id)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null) return NotFound("El usuario no existe.");
            return Ok(user);
        }

        /// <summary>
        /// Updates an existing user.
        /// </summary>
        /// <param name="id">The user identifier.</param>
        /// <param name="updatedUser">The updated user entity.</param>
        /// <returns>Success message if updated, otherwise 404.</returns>
        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateUser(string id, [FromBody] User updatedUser)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null) return NotFound("El usuario no existe.");

            user.Name = updatedUser.Name ?? user.Name;
            user.Email = updatedUser.Email ?? user.Email;
            user.Role = updatedUser.Role ?? user.Role;

            await _context.SaveChangesAsync();
            return Ok("El usuario fue actualizado exitosamente.");
        }

        /// <summary>
        /// Deletes a user by their identifier. Only accessible by users with the AdminPolicy.
        /// </summary>
        /// <param name="id">The user identifier.</param>
        /// <returns>Success message if deleted, otherwise 404.</returns>
        [HttpDelete("{id}")]
        [Authorize(Policy = "AdminPolicy")]
        public async Task<IActionResult> DeleteUser(string id)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null) return NotFound("El usuario no existe.");

            _context.Users.Remove(user);
            await _context.SaveChangesAsync();
            return Ok("El usuario fue eliminado exitosamente.");
        }
    }
}