using EduconnectAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace EduconnectAPI.Config
// Class to configure the database context (The database connection)
{
    public class ApplicationDbContext : DbContext
    {
        //Constructor
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : base(options)
        { }

        //DbSets (Tables in the database)
        public DbSet<User> Users { get; set; } = null!;
        public DbSet<Course> Courses { get; set; } = null!;
        public DbSet<Enrollment> Enrollments { get; set; } = null!;
        public DbSet<Models.Task> Tasks { get; set; } = null!;
        public DbSet<Notification> Notifications { get; set; } = null!;

        //Method to configure the models and relationships 
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {

            modelBuilder.Entity<Enrollment>()
                .HasOne(e => e.User)
                .WithMany(u => u.Enrollments) // One to many relationship config
                .HasForeignKey(e => e.UserId);

            modelBuilder.Entity<Enrollment>()
                .HasOne(e => e.Course)
                .WithMany(c => c.Enrollments)
                .HasForeignKey(e => e.CourseId);

            modelBuilder.Entity<Models.Task>()
                .HasOne(t => t.Course)
                .WithMany(c => c.Tasks)
                .HasForeignKey(t => t.CourseId);

            modelBuilder.Entity<Notification>()
                .HasOne(n => n.User)
                .WithMany(u => u.Notifications)
                .HasForeignKey(n => n.UserId);

            base.OnModelCreating(modelBuilder);

        }

    }
}

