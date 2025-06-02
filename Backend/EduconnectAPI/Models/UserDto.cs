namespace EduconnectAPI.Models;

/// <summary>
/// Data transfer object for user authentication.
/// </summary>
public class UserDto
{
    /// <summary>
    /// The identifier of the user.
    /// </summary>
    public required string Id { get; set; }

    /// <summary>
    /// The password of the user.
    /// </summary>
    public required string Password { get; set; }

    /// <summary>
    /// The role of the user (optional).
    /// </summary>
    public string Role { get; set; }
}