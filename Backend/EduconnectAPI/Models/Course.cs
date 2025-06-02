namespace EduconnectAPI.Models
{
    /// <summary>
    /// Represents a course in the system.
    /// </summary>
    public class Course
    {
        /// <summary>
        /// The unique identifier of the course.
        /// </summary>
        public int Id { get; set; }

        /// <summary>
        /// The name of the course.
        /// </summary>
        public string Name { get; set; } = string.Empty;

        /// <summary>
        /// The description of the course.
        /// </summary>
        public string Description { get; set; } = string.Empty;

        /// <summary>
        /// The identifier of the user who created the course.
        /// </summary>
        public string CreatorId { get; set; } = string.Empty;

        /// <summary>
        /// The date and time when the course was created.
        /// </summary>
        public DateTime CreadedAt { get; set; }

        /// <summary>
        /// The user who created the course.
        /// </summary>
        public User Creator { get; set; } = null!;

        /// <summary>
        /// The enrollments associated with the course.
        /// </summary>
        public ICollection<Enrollment> Enrollments { get; set; } = new List<Enrollment>();

        /// <summary>
        /// The tasks associated with the course.
        /// </summary>
        public ICollection<Task> Tasks { get; set; } = new List<Task>();
    }
}