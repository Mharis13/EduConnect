namespace EduconnectAPI.Models
{
    /// <summary>
    /// Represents the enrollment of a user in a course.
    /// </summary>
    public class Enrollment
    {
        /// <summary>
        /// The unique identifier of the enrollment.
        /// </summary>
        public int Id { get; set; }

        /// <summary>
        /// The identifier of the enrolled user.
        /// </summary>
        public string UserId { get; set; } = null!;

        /// <summary>
        /// The identifier of the course.
        /// </summary>
        public int CourseId { get; set; }

        /// <summary>
        /// The date and time when the enrollment was created.
        /// </summary>
        public DateTime EnrolledAt { get; set; }

        /// <summary>
        /// The user associated with the enrollment.
        /// </summary>
        public User User { get; set; } = null!;

        /// <summary>
        /// The course associated with the enrollment.
        /// </summary>
        public Course Course { get; set; } = null!;
    }
}