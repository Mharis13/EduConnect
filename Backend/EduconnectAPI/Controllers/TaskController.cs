using EduconnectAPI.Config;
            using EduconnectAPI.Models;
            using Microsoft.AspNetCore.Authorization;
            using Microsoft.AspNetCore.Mvc;
            using Microsoft.EntityFrameworkCore;
            using EduTask =  EduconnectAPI.Models.Task;
            
            namespace EduconnectAPI.Controllers
            {
                /// <summary>
                /// Controller responsible for managing tasks related to courses.
                /// </summary>
                [Route("api/v1/task")]
                [ApiController]
                public class TaskController : ControllerBase
                {
                    private readonly ApplicationDbContext _context;
            
                    /// <summary>
                    /// Initializes a new instance of the <see cref="TaskController"/> class.
                    /// </summary>
                    /// <param name="context">The application database context.</param>
                    public TaskController(ApplicationDbContext context)
                    {
                        _context = context;
                    }
            
                    /// <summary>
                    /// Retrieves all tasks in the system.
                    /// </summary>
                    /// <returns>A list of all tasks as TaskDto objects.</returns>
                    [HttpGet]
                    public async Task<IActionResult> GetAllTasks()
                    {
                        var tasks = await _context.Tasks
                            .Select(t => new TaskDto
                            {
                                Id = t.Id,
                                Title = t.Title,
                                Description = t.Description,
                                DueDate = t.DueDate,
                                CourseId = t.CourseId
                            })
                            .ToListAsync();
                    
                        return Ok(tasks);
                    }
            
                    /// <summary>
                    /// Retrieves a specific task by its identifier.
                    /// </summary>
                    /// <param name="id">The task identifier.</param>
                    /// <returns>The task if found, otherwise 404.</returns>
                    [HttpGet("{id}")]
                    public async Task<IActionResult> GetTaskById(int id)
                    {
                        var task = await _context.Tasks.Include(t => t.Course).FirstOrDefaultAsync(t => t.Id == id);
                        if (task == null) return NotFound("La tarea no existe.");
                        return Ok(task);
                    }
            
                    /// <summary>
                    /// Creates a new task. Only accessible by users with the ProfessorPolicy.
                    /// </summary>
                    /// <param name="task">The task entity to create.</param>
                    /// <returns>The created task.</returns>
                    [HttpPost]
                    [Authorize(Policy = "ProfessorPolicy")]
                    public async Task<IActionResult> CreateTask([FromBody] EduTask task)
                    {
                        _context.Tasks.Add(task);
                        await _context.SaveChangesAsync();
                        return CreatedAtAction(nameof(GetTaskById), new { id = task.Id }, task);
                    }
            
                    /// <summary>
                    /// Updates an existing task. Only accessible by users with the ProfessorPolicy.
                    /// </summary>
                    /// <param name="id">The task identifier.</param>
                    /// <param name="updatedTask">The updated task entity.</param>
                    /// <returns>Success message if updated, otherwise 404.</returns>
                    [HttpPut("{id}")]
                    [Authorize(Policy = "ProfessorPolicy")]
                    public async Task<IActionResult> UpdateTask(int id, [FromBody] EduTask updatedTask)
                    {
                        var task = await _context.Tasks.FindAsync(id);
                        if (task == null) return NotFound("La tarea no existe.");
            
                        task.Title = updatedTask.Title ?? task.Title;
                        task.Description = updatedTask.Description ?? task.Description;
                        task.DueDate = updatedTask.DueDate ?? task.DueDate;
            
                        await _context.SaveChangesAsync();
                        return Ok("La tarea fue actualizada exitosamente.");
                    }
            
                    /// <summary>
                    /// Deletes a task by its identifier. Only accessible by users with the ProfessorPolicy.
                    /// </summary>
                    /// <param name="id">The task identifier.</param>
                    /// <returns>Success message if deleted, otherwise 404.</returns>
                    [HttpDelete("{id}")]
                    [Authorize(Policy = "ProfessorPolicy")]
                    public async Task<IActionResult> DeleteTask(int id)
                    {
                        var task = await _context.Tasks.FindAsync(id);
                        if (task == null) return NotFound("La tarea no existe.");
            
                        _context.Tasks.Remove(task);
                        await _context.SaveChangesAsync();
                        return Ok("La tarea fue eliminada exitosamente.");
                    }
                }
            }