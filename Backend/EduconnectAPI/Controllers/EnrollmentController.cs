using EduconnectAPI.Config;
using EduconnectAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace EduconnectAPI.Controllers
{
    /// <summary>
    /// Controller responsible for managing enrollments between users and courses.
    /// </summary>
    [Route("api/v1/enrollment")]
    [ApiController]
    public class EnrollmentController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        /// <summary>
        /// Initializes a new instance of the <see cref="EnrollmentController"/> class.
        /// </summary>
        /// <param name="context">The application database context.</param>
        public EnrollmentController(ApplicationDbContext context)
        {
            _context = context;
        }

        /// <summary>
        /// Retrieves all enrollments, including related user and course information.
        /// Only accessible by users with the ProfessorPolicy.
        /// </summary>
        /// <returns>A list of all enrollments.</returns>
        [HttpGet]
        [Authorize(Policy = "ProfessorPolicy")]
        public async Task<IActionResult> GetAllEnrollments()
        {
            var enrollments = await _context.Enrollments
                .Include(e => e.User)
                .Include(e => e.Course)
                .ToListAsync();

            return Ok(enrollments);
        }

        /// <summary>
        /// Enrolls a student in a course.
        /// Only accessible by users with the ProfessorPolicy.
        /// </summary>
        /// <param name="enrollment">The enrollment object containing user and course IDs.</param>
        /// <returns>The created enrollment.</returns>
        [HttpPost]
        [Authorize(Policy = "ProfessorPolicy")]
        public async Task<IActionResult> EnrollStudent([FromBody] Enrollment enrollment)
        {
            _context.Enrollments.Add(enrollment);
            await _context.SaveChangesAsync();

            return Created($"{Url.RouteUrl(RouteData.Values)}{enrollment.Id}", enrollment);
        }

        /// <summary>
        /// Removes an enrollment by its identifier.
        /// Only accessible by users with the ProfessorPolicy.
        /// </summary>
        /// <param name="id">The enrollment identifier.</param>
        /// <returns>Ok if removed, or 404 if not found.</returns>
        [HttpDelete("{id}")]
        [Authorize(Policy = "ProfessorPolicy")]
        public async Task<IActionResult> RemoveEnrollment(int id)
        {
            var enrollment = await _context.Enrollments.FindAsync(id);
            if (enrollment == null) return NotFound("The enrollment does not exist.");

            _context.Enrollments.Remove(enrollment);
            await _context.SaveChangesAsync();
            return Ok();
        }
    }
}