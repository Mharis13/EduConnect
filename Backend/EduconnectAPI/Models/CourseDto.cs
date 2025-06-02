namespace EduconnectAPI.Models;

/// <summary>
/// Data transfer object for a course.
/// </summary>
public class CourseDto
{
    /// <summary>
    /// The name of the course.
    /// </summary>
    public string Name { get; set; }

    /// <summary>
    /// The description of the course.
    /// </summary>
    public string Description { get; set; }
}