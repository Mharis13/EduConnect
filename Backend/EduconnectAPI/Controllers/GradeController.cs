using EduconnectAPI.Models;
using EduconnectAPI.Config;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Threading.Tasks;

namespace EduconnectAPI.Controllers
{
    /// <summary>
    /// Controller responsible for managing grades and task submissions.
    /// </summary>
    [Route("api/v1/grade")]
    [ApiController]
    [Authorize]
    public class GradeController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        /// <summary>
        /// Initializes a new instance of the <see cref="GradeController"/> class.
        /// </summary>
        /// <param name="context">The application database context.</param>
        public GradeController(ApplicationDbContext context)
        {
            _context = context;
        }

        /// <summary>
        /// Retrieves all grades and their associated links.
        /// </summary>
        /// <returns>A list of grades with task, user, link, and score information.</returns>
        [HttpGet]
        public async Task<IActionResult> GetAllGradesAndLinks()
        {
            var grades = await _context.Grades
                .Select(g => new
                {
                    g.Id,
                    g.TaskId,
                    g.UserId,
                    g.Link,
                    g.Score
                })
                .ToListAsync();

            return Ok(grades);
        }

        /// <summary>
        /// Allows a student to submit or update a link for a task.
        /// </summary>
        /// <param name="submission">The task submission data transfer object.</param>
        /// <returns>Success message if the link is submitted or updated.</returns>
        [HttpPost("submit")]
        [Authorize(Roles = "Student")]
        public async Task<IActionResult> SubmitTaskLink([FromBody] TaskSubmissionDto submission)
        {
            var grade = await _context.Grades
                .FirstOrDefaultAsync(g => g.TaskId == submission.TaskId && g.UserId == submission.UserId);

            if (grade == null)
            {
                grade = new Grade
                {
                    TaskId = submission.TaskId,
                    UserId = submission.UserId,
                    Link = submission.Link
                };
                _context.Grades.Add(grade);
            }
            else
            {
                grade.Link = submission.Link;
            }

            await _context.SaveChangesAsync();
            return Ok("Link submitted successfully.");
        }

        /// <summary>
        /// Allows a teacher to assign a grade to a student's task submission.
        /// </summary>
        /// <param name="gradeDto">The grade submission data transfer object.</param>
        /// <returns>Success message if the grade is assigned, or 404 if the submission is not found.</returns>
        [HttpPost("grade")]
        [Authorize(Roles = "Teacher")]
        public async Task<IActionResult> GradeTask([FromBody] GradeSubmissionDto gradeDto)
        {
            var grade = await _context.Grades
                .FirstOrDefaultAsync(g => g.TaskId == gradeDto.TaskId && g.UserId == gradeDto.UserId);

            if (grade == null)
                return NotFound("Submission not found.");

            grade.Score = gradeDto.Score;
            await _context.SaveChangesAsync();
            return Ok("Grade assigned successfully.");
        }
    }
}

/// <summary>
/// Data transfer object for submitting a task link.
/// </summary>
public class TaskSubmissionDto
{
    /// <summary>
    /// The identifier of the task.
    /// </summary>
    public int TaskId { get; set; }

    /// <summary>
    /// The identifier of the user.
    /// </summary>
    public string UserId { get; set; }

    /// <summary>
    /// The link to the submitted task.
    /// </summary>
    public string Link { get; set; }
}

/// <summary>
/// Data transfer object for submitting a grade.
/// </summary>
public class GradeSubmissionDto
{
    /// <summary>
    /// The identifier of the task.
    /// </summary>
    public int TaskId { get; set; }

    /// <summary>
    /// The identifier of the user.
    /// </summary>
    public string UserId { get; set; }

    /// <summary>
    /// The score assigned to the task.
    /// </summary>
    public decimal Score { get; set; }
}