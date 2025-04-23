namespace EduconnectAPI.Models
{
    public class Task
    {
        public int Id { get; set; }
        public string Title { get; set; } = null!;
        public string? Description { get; set; }
        public int CourseId { get; set; }
        public DateTime? DueDate { get; set; }

        // Relaciones
        public Course Course { get; set; } = null!;
    }
}
