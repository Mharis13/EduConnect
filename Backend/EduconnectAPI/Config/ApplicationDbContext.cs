using EduconnectAPI.Models;
        using Microsoft.EntityFrameworkCore;
        
        namespace EduconnectAPI.Config
        {
            public class ApplicationDbContext : DbContext
            {
                public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : base(options) { }
        
                public DbSet<User> Users { get; set; } = null!;
                public DbSet<Course> Courses { get; set; } = null!;
                public DbSet<Enrollment> Enrollments { get; set; } = null!;
                public DbSet<Models.Task> Tasks { get; set; } = null!;
                public DbSet<Notification> Notifications { get; set; } = null!;
                public DbSet<Grade> Grades { get; set; } = null!; // Nuevo DbSet
        
                protected override void OnModelCreating(ModelBuilder modelBuilder)
                {
                    modelBuilder.Entity<Enrollment>()
                        .HasOne(e => e.User)
                        .WithMany(u => u.Enrollments)
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
        
                    modelBuilder.Entity<Course>()
                        .HasOne(c => c.Creator)
                        .WithMany(u => u.CreatedCourses)
                        .HasForeignKey(c => c.CreatorId)
                        .OnDelete(DeleteBehavior.Cascade);
        
                    // Relación Grade - User
                    modelBuilder.Entity<Grade>()
                        .HasOne(g => g.User)
                        .WithMany(u => u.Grades)
                        .HasForeignKey(g => g.UserId);
        
                    // Relación Grade - Task
                    modelBuilder.Entity<Grade>()
                        .HasOne(g => g.Task)
                        .WithMany(t => t.Grades)
                        .HasForeignKey(g => g.TaskId);
        
                    base.OnModelCreating(modelBuilder);
                }
            }
        }