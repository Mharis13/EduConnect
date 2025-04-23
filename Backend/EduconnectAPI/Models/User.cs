namespace EduconnectAPI.Models
{
    public class User
    {
        public int Id { get; set; }
        public string Name { get; set; } = null!;
        public string Email { get; set; } = null!;
        public string PasswordHash { get; set; } = null!;
        public string Role { get; set; } = null!;
        public DateTime CreateAt { get; set; }

        //Relations
        public ICollection<Course> CreatedCourses { get; set; } = new List<Course>();
        public ICollection<Enrollment> Enrollments { get; set; } = new List<Enrollment>();
        public ICollection<Notification> Notifications { get; set; } = new List<Notification>();

    }
}
