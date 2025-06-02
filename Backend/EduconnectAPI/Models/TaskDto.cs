namespace EduconnectAPI.Models;

/// <summary>
/// Data transfer object for a task.
/// </summary>
public class TaskDto
{
    /// <summary>
    /// The unique identifier of the task (optional, useful for responses).
    /// </summary>
    public int? Id { get; set; }

    /// <summary>
    /// The title of the task.
    /// </summary>
    public string Title { get; set; } = null!;

    /// <summary>
    /// The description of the task (optional).
    /// </summary>
    public string? Description { get; set; }

    /// <summary>
    /// The identifier of the course to which the task belongs.
    /// </summary>
    public int CourseId { get; set; }

    /// <summary>
    /// The due date of the task (optional).
    /// </summary>
    public DateTime? DueDate { get; set; }
}