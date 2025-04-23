namespace EduconnectAPI.Models
{
    public class Course
    {
        public int Id { get; set; }
        public string Name { get; set; } = null!;
        public string? Description { get; set; }
        public int CreatedBy { get; set; }
        public DateTime CreadedAt { get; set; }

        //Relations
        public User Creator { get; set; } = null!;
        public ICollection<Enrollment> Enrollments { get; set; } = new List<Enrollment>();
        public ICollection<Task> Tasks { get; set; } = new List<Task>();
    }
}
