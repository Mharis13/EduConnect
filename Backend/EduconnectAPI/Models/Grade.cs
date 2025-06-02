namespace EduconnectAPI.Models;

/// <summary>
/// Represents a grade for a task assigned to a user.
/// </summary>
public class Grade
{
    /// <summary>
    /// The unique identifier of the grade.
    /// </summary>
    public int Id { get; set; }

    /// <summary>
    /// The identifier of the user (student).
    /// </summary>
    public string UserId { get; set; }

    /// <summary>
    /// The identifier of the task.
    /// </summary>
    public int TaskId { get; set; }

    /// <summary>
    /// The score assigned to the task (optional).
    /// </summary>
    public decimal? Score { get; set; }

    /// <summary>
    /// The link to the submitted task (optional).
    /// </summary>
    public string? Link { get; set; }

    /// <summary>
    /// The user associated with the grade.
    /// </summary>
    public User User { get; set; }

    /// <summary>
    /// The task associated with the grade.
    /// </summary>
    public Task Task { get; set; }
}