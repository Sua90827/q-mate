export interface Chart {
  matchId: number;
  month: string;
  from: Date;
  to: Date;
  totalLikes: number;
  categories: ChartCategories[];
}

export interface ChartCategories {
  categoryId: number;
  categoryName: string;
  likeCount: number;
}
