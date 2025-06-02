namespace EduconnectAPI.Models
{
    /// <summary>
    /// Represents a task assigned in a course.
    /// </summary>
    public class Task
    {
        /// <summary>
        /// The unique identifier of the task.
        /// </summary>
        public int Id { get; set; }

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

        /// <summary>
        /// The course associated with the task.
        /// </summary>
        public Course Course { get; set; } = null!;

        /// <summary>
        /// The grades associated with the task.
        /// </summary>
        public ICollection<Grade> Grades { get; set; } = [];
    }
}