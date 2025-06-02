namespace EduconnectAPI.Models
{
    /// <summary>
    /// Represents a notification sent to a user.
    /// </summary>
    public class Notification
    {
        /// <summary>
        /// The unique identifier of the notification.
        /// </summary>
        public int Id { get; set; }

        /// <summary>
        /// The identifier of the user who receives the notification.
        /// </summary>
        public string UserId { get; set; } = string.Empty;

        /// <summary>
        /// The message content of the notification.
        /// </summary>
        public string Message { get; set; } = string.Empty;

        /// <summary>
        /// Indicates whether the notification has been read.
        /// </summary>
        public bool IsRead { get; set; }

        /// <summary>
        /// The date and time when the notification was created.
        /// </summary>
        public DateTime CreatedAt { get; set; }

        /// <summary>
        /// The user associated with the notification.
        /// </summary>
        public required User User { get; set; }
    }
}