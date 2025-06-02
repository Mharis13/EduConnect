namespace EduconnectAPI.Models
{
    /// <summary>
    /// Represents a user in the system.
    /// </summary>
    public class User
    {
        /// <summary>
        /// The unique identifier of the user.
        /// </summary>
        public string Id { get; init; } = Guid.NewGuid().ToString();

        /// <summary>
        /// The name of the user.
        /// </summary>
        public string Name { get; set; } = string.Empty;

        /// <summary>
        /// The email address of the user.
        /// </summary>
        public string Email { get; set; } = string.Empty;

        /// <summary>
        /// The hashed password of the user.
        /// </summary>
        public string PasswordHash { get; set; } = string.Empty;

        /// <summary>
        /// The role of the user (e.g., Student, Teacher, Admin).
        /// </summary>
        public string Role { get; set; } = string.Empty;

        /// <summary>
        /// The date and time when the user was created.
        /// </summary>
        public DateTime CreateAt { get; set; }

        /// <summary>
        /// The courses created by the user.
        /// </summary>
        public ICollection<Course> CreatedCourses { get; init; } = [];

        /// <summary>
        /// The enrollments of the user.
        /// </summary>
        public ICollection<Enrollment> Enrollments { get; init; } = [];

        /// <summary>
        /// The notifications received by the user.
        /// </summary>
        public ICollection<Notification> Notifications { get; init; } = [];

        /// <summary>
        /// The grades received by the user.
        /// </summary>
        public ICollection<Grade> Grades { get; set; } = [];
    }
}