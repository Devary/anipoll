import {RecognitionException} from "antlr4";

export class EntityRegistry{
  public static SHARACTER : string = "Character"
  public static ROLE : string = "Role"
  public static ANIME : string = "Anime"
  public static POLL : string = "poll"


  public static getByName(name: string): EntityRegistry {
    let _name = name.toLowerCase()
    switch (_name) {
      case "role": return this.ROLE
      case "sharacter": return this.SHARACTER
      case "poll": return this.POLL
      case "anime": return this.ANIME
      default : throw new SyntaxError("Cannot determine entity name");
    }
  }

}
